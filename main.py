# main.py

import pandas as pd
import time
import os
from data.input_loader import load_field_dict, load_appendix_data
from data.java_loader import get_all_java_files
from prompts.logic_prompt_builder import (
    load_few_shot_examples,
    build_few_shot_block,
    build_prompt
)
from api.deepseek_client import call_deepseek
from config import (
    INPUT_MODE,
    EXCEL_PATH,
    OUTPUT_PATH,
    DICT_PATH,
    FEW_SHOT_PATH,
    SHEET_NAME,
    START_ROW,
    MAX_ROWS,
    APPENDIX_SHEET,
    DICT_SHEET,
    REQUEST_INTERVAL
)

# === 开始计时 ===
total_start_time = time.time()

# === 公共数据加载 ===
field_dict = load_field_dict(DICT_PATH, sheet=DICT_SHEET)
appendix_data = load_appendix_data(DICT_PATH, sheet=APPENDIX_SHEET)
few_shot_df = load_few_shot_examples(FEW_SHOT_PATH)
few_shot_block = build_few_shot_block(few_shot_df)
os.makedirs("output", exist_ok=True)

# === 模式一：Excel输入 ===
if INPUT_MODE == "excel":
    print("当前模式：从 Excel 中读取变量")
    
    # 支持断点续跑
    if os.path.exists(OUTPUT_PATH):
        full_df = pd.read_excel(OUTPUT_PATH)
        print("检测到已有输出文件，将从中断位置继续处理")
    else:
        full_df = pd.read_excel(EXCEL_PATH, sheet_name=SHEET_NAME)
    
    for i in range(START_ROW, len(full_df)):
        row = full_df.iloc[i]
        var_name = row.get("中文名")
        java_code = row.get("代码")

        if pd.isna(var_name) or pd.isna(java_code):
            continue

        print(f"正在处理第 {i + 1} 行变量：{var_name} ...")
        start_time = time.time()
        try:
            prompt = build_prompt(var_name, java_code, field_dict, appendix_data, few_shot_block)
            result = call_deepseek(prompt)
            end_time = time.time()
            duration = round(end_time - start_time, 2)
            print(f"第 {i + 1} 行完成，用时 {duration} 秒")
            print(f"输出：{result}\n")

            # 保存结果
            full_df.at[i, "取值逻辑-模型"] = result
            full_df.to_excel(OUTPUT_PATH, index=False, sheet_name=SHEET_NAME)

            time.sleep(REQUEST_INTERVAL)

        except Exception as e:
            print(f"第 {i + 1} 行失败：{e}")
            continue

        if MAX_ROWS and (i - START_ROW + 1) >= MAX_ROWS:
            break

# === 模式二：Java代码输入 ===
elif INPUT_MODE == "java":
    print("当前模式：从 vars 文件夹读取 Java 代码")
    java_file_list = get_all_java_files("vars")

    # 尝试读取已有输出（如果存在则续写）
    if os.path.exists(OUTPUT_PATH):
        full_df = pd.read_excel(OUTPUT_PATH)
        processed_files = set(full_df["代码文件"].tolist())
        print(f"已存在输出文件，将跳过已处理的 {len(processed_files)} 个文件")
    else:
        full_df = pd.DataFrame(columns=["变量名", "代码文件", "代码", "取值逻辑-模型"])
        processed_files = set()

    for i, item in enumerate(java_file_list):
        file_name = item["file_name"]

        if file_name in processed_files:
            continue  # 跳过已处理的文件
        if MAX_ROWS and i >= MAX_ROWS:
            break
        if i < START_ROW:
            continue

        var_name = file_name.replace(".java", "")
        java_code = item["java_code"]

        print(f"正在处理：{file_name}")
        start_time = time.time()
        try:
            prompt = build_prompt(var_name, java_code, field_dict, appendix_data, few_shot_block)
            result = call_deepseek(prompt)
            end_time = time.time()
            duration = round(end_time - start_time, 2)
            print(f"处理完成：{file_name}，耗时 {duration} 秒")
            print(f"输出：{result}\n")

            # 写入 DataFrame 并立即保存
            new_row = {
                "变量名": var_name,
                "代码文件": file_name,
                "代码": java_code,
                "取值逻辑-模型": result
            }
            full_df = pd.concat([full_df, pd.DataFrame([new_row])], ignore_index=True)
            full_df.to_excel(OUTPUT_PATH, index=False)

            time.sleep(REQUEST_INTERVAL)

        except Exception as e:
            print(f"处理失败：{file_name}，错误：{e}")
            continue

    print(f"Java 文件处理完成，共生成 {len(full_df)} 条结果")


# === 完成 ===
total_end_time = time.time()
total_duration = round(total_end_time - total_start_time, 2)
print(f"全部处理完成，总耗时：{total_duration} 秒")
print(f"结果已保存至：{OUTPUT_PATH}")
