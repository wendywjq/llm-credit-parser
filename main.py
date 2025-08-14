# main.py
# -----------------------------------------------------------------------------
# Orchestrates two input modes:
#   1) Excel mode: read variable name + Java code from an Excel sheet
#   2) Java mode: recursively read all .java files under VARS_DIR and process each
#
# Features:
#   - Loads field dictionary + appendix (code lists) for prompt context
#   - Loads few-shot examples and builds a few-shot block
#   - Injects ONLY the actually used helper content into the prompt header:
#       * ParameterMapping: used method bodies (stripped of comments/blank lines)
#       * Utils: used method signatures only (no bodies)
#       * Enums: LoanBusinessType / StatType / CalcAcctType / CurrencyType (if found)
#   - Auto-saves after each item to enable resumable execution
#   - Prints per-item and total processing times
# -----------------------------------------------------------------------------

import os
import time
import pandas as pd

from data.input_loader import load_field_dict, load_appendix_data
from data.java_loader import get_all_java_files, load_header_files, load_param_sources
from prompts.logic_prompt_builder import (
    load_few_shot_examples,
    build_few_shot_block,
    build_prompt,
)
from utils.header_used_bodies import build_used_methods_header_block
from api.deepseek_client import call_deepseek

from config import (
    INPUT_MODE,           # "excel" | "java"
    EXCEL_PATH,
    OUTPUT_PATH,
    DICT_PATH,
    FEW_SHOT_PATH,
    SHEET_NAME,           # Excel sheet name, e.g. "算话变量" / "项目变量"
    START_ROW,            # start index for resume
    MAX_ROWS,             # max items to process (None = all)
    APPENDIX_SHEET,       # e.g., "附录"
    DICT_SHEET,           # e.g., "征信合表"
    REQUEST_INTERVAL,     # seconds between requests
    HEADER_FILES,         # header file paths (ParameterMapping.java, Utils.java, ...)
    MAX_HEADER_CHARS,     # char cap for header block in prompt
    VARS_DIR,             # base folder for Java mode, e.g., "vars"
    PARAMS_DIR
)

# -----------------------------------------------------------------------------
# Helpers
# -----------------------------------------------------------------------------

def _ensure_output_dir():
    """Ensure parent folder of OUTPUT_PATH exists."""
    out_dir = os.path.dirname(OUTPUT_PATH) or "."
    os.makedirs(out_dir, exist_ok=True)


def _elapsed(prefix: str, start_ts: float) -> float:
    """Print elapsed time with prefix and return elapsed seconds."""
    sec = round(time.time() - start_ts, 2)
    print(f"{prefix}{sec} 秒")
    return sec


# -----------------------------------------------------------------------------
# Bootstrap (shared resources)
# -----------------------------------------------------------------------------

total_start = time.time()
_ensure_output_dir()

print("加载字段字典与附录 ...")
field_dict_df = load_field_dict(DICT_PATH, sheet=DICT_SHEET)
appendix_df = load_appendix_data(DICT_PATH, sheet=APPENDIX_SHEET)

print("加载 few-shot 示例 ...")
# 若需根据 sheet 类型设置 few-shot 起始行，可在 load_few_shot_examples 中处理 start_row
few_shot_df = load_few_shot_examples(FEW_SHOT_PATH, sheet=SHEET_NAME, max_examples=4, start_row=0)
few_shot_block = build_few_shot_block(few_shot_df)

print("预加载头文件源码 ...")
headers_src = load_header_files(HEADER_FILES)  # {filename: source_code}
param_sources = load_param_sources(PARAMS_DIR) # {class_name: source_code}

# -----------------------------------------------------------------------------
# Mode 1: Excel input
# -----------------------------------------------------------------------------
if INPUT_MODE.lower() == "excel":
    print("当前模式：Excel 输入")

    # Resume from existing OUTPUT if present
    if os.path.exists(OUTPUT_PATH):
        full_df = pd.read_excel(OUTPUT_PATH)
        print("检测到已有输出文件，将从中断位置继续处理")
    else:
        full_df = pd.read_excel(EXCEL_PATH, sheet_name=SHEET_NAME)

    total_rows = len(full_df)
    print(f"共检测到 {total_rows} 条记录；从第 {START_ROW + 1} 条开始处理")

    for i in range(START_ROW, total_rows):
        row = full_df.iloc[i]
        var_name = row.get("中文名")
        java_code = row.get("代码")

        if pd.isna(var_name) or pd.isna(java_code):
            continue

        print(f"\n处理第 {i + 1} 条：{var_name}")
        t0 = time.time()

        try:
            # 动态构建头文件块：ParameterMapping=方法体，Utils=签名
            header_block = build_used_methods_header_block(
                java_code=java_code,
                headers=headers_src,
                enum_candidates=("LoanBusinessType", "StatType", "CalcAcctType", "CurrencyType"),
                strip_comments=True,
                max_chars=MAX_HEADER_CHARS,
                class_policy={"ParameterMapping": "bodies", "Utils": "signatures"},
                param_sources=param_sources,
            )

            prompt = build_prompt(
                variable_name=var_name,
                java_code=java_code,
                field_dict_df=field_dict_df,
                appendix_data_df=appendix_df,
                few_shot_block=few_shot_block,
                header_block=header_block,
            )

            result = call_deepseek(prompt)
            _elapsed("该条处理用时：", t0)

            # Write-through to avoid loss on interruption
            full_df.at[i, "取值逻辑-模型"] = result
            full_df.to_excel(OUTPUT_PATH, index=False, sheet_name=SHEET_NAME)

            time.sleep(REQUEST_INTERVAL)

        except Exception as e:
            print(f"第 {i + 1} 条处理失败：{e}")
            continue

        if MAX_ROWS and (i - START_ROW + 1) >= MAX_ROWS:
            print(f"达到 MAX_ROWS={MAX_ROWS} 条，提前结束")
            break

# -----------------------------------------------------------------------------
# Mode 2: Java input (scan VARS_DIR recursively)
# -----------------------------------------------------------------------------
elif INPUT_MODE.lower() == "java":
    print("当前模式：Java 输入（递归扫描目录）")
    java_files = get_all_java_files(VARS_DIR)  # list of dicts: {file_path, file_name, java_code}
    print(f"目录 {VARS_DIR} 下共发现 {len(java_files)} 个 .java 文件")

    # Resume from existing OUTPUT if present; prefer '文件路径' column to avoid name collisions
    if os.path.exists(OUTPUT_PATH):
        out_df = pd.read_excel(OUTPUT_PATH)
        if "代码文件" in out_df.columns:
            processed = set(out_df["代码文件"].dropna().astype(str).tolist())
        else:
            processed = set()
        print(f"已有输出记录：{len(processed)} 条，将跳过已处理文件")
    else:
        out_df = pd.DataFrame(columns=["变量名", "代码文件", "代码", "取值逻辑-模型"])
        processed = set()

    produced = 0
    for idx, item in enumerate(java_files):
        if MAX_ROWS and produced >= MAX_ROWS:
            print(f"达到 MAX_ROWS={MAX_ROWS} 条，提前结束")
            break
        if idx < START_ROW:
            continue

        file_path = item["file_path"]
        file_name = item["file_name"]
        java_code = item["java_code"]

        # Skip already processed by full path, fallback to file name
        if file_path in processed or file_name in processed:
            continue

        var_name = os.path.splitext(file_name)[0]
        print(f"\n处理文件：{file_path}")
        t0 = time.time()

        try:
            header_block = build_used_methods_header_block(
                java_code=java_code,
                headers=headers_src,
                enum_candidates=("LoanBusinessType", "StatType", "CalcAcctType", "CurrencyType"),
                strip_comments=True,
                max_chars=MAX_HEADER_CHARS,
                class_policy={"ParameterMapping": "bodies", "Utils": "signatures"},
                param_sources=param_sources,
            )

            prompt = build_prompt(
                variable_name=var_name,
                java_code=java_code,
                field_dict_df=field_dict_df,
                appendix_data_df=appendix_df,
                few_shot_block=few_shot_block,
                header_block=header_block,
            )

            result = call_deepseek(prompt)
            print("输出结果：", result)
            _elapsed("该条处理用时：", t0)

            # Append and persist immediately
            new_row = {
                "变量名": var_name,
                "代码文件": file_name,
                "代码": java_code,
                "取值逻辑-模型": result,
            }
            out_df = pd.concat([out_df, pd.DataFrame([new_row])], ignore_index=True)
            out_df.to_excel(OUTPUT_PATH, index=False)

            produced += 1
            time.sleep(REQUEST_INTERVAL)

        except Exception as e:
            print(f"处理失败：{file_path}，错误：{e}")
            continue

else:
    raise ValueError(f"不支持的 INPUT_MODE：{INPUT_MODE}，请在 config.py 中设置为 'excel' 或 'java'")

# -----------------------------------------------------------------------------
# Done
# -----------------------------------------------------------------------------
elapsed_total = _elapsed("\n全部处理完成，总耗时：", total_start)
print(f"结果已保存至：{OUTPUT_PATH}")
