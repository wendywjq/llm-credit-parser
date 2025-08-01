# main.py
import pandas as pd
import time
import os
from data.input_loader import load_field_dict, load_appendix_data
from prompts.logic_prompt_builder import load_few_shot_examples, build_messages
from api.deepseek_client import call_deepseek
from config import (
    EXCEL_PATH, OUTPUT_PATH, DICT_PATH, FEW_SHOT_PATH,
    SHEET_NAME, START_ROW, MAX_ROWS, APPENDIX_SHEET, DICT_SHEET
)

# === 开始计时 ===
total_start_time = time.time()

# === 加载数据 ===
if os.path.exists(OUTPUT_PATH):
    full_df = pd.read_excel(OUTPUT_PATH)
    print(f"检测到已有输出文件，将从中断位置继续处理")
else:
    full_df = pd.read_excel(EXCEL_PATH, sheet_name=SHEET_NAME)  

field_dict = load_field_dict(DICT_PATH, sheet=DICT_SHEET)
appendix_data = load_appendix_data(DICT_PATH, sheet=APPENDIX_SHEET)
few_shot_messages = load_few_shot_examples(FEW_SHOT_PATH)
os.makedirs("output", exist_ok=True)

# === 遍历变量，生成取值逻辑 ===
for i in range(START_ROW, len(full_df)):
    row = full_df.iloc[i]
    var_name = row.get("中文名")
    java_code = row.get("代码")
    
    if pd.isna(var_name) or pd.isna(java_code):
        continue

    print(f"正在处理第 {i + 1} 行变量：{var_name} ...")
    start_time = time.time()
    try:
        messages = build_messages(var_name, java_code, field_dict, appendix_data, few_shot_messages)
        result = call_deepseek(messages)  # 修改为直接传递messages
        end_time = time.time()
        duration = round(end_time - start_time, 2)
        print(f"第 {i + 1} 行处理完成，用时 {duration} 秒")
        print(f"运行结果：{result}")
        print()
        full_df.at[i, "取值逻辑-模型"] = result
        full_df.to_excel(OUTPUT_PATH, index=False, sheet_name=SHEET_NAME)
        time.sleep(2)
    except Exception as e:
        print(f"第 {i + 1} 行处理失败：{e}")
        continue

    if MAX_ROWS and (i - START_ROW + 1) >= MAX_ROWS:
        break

# === 处理完成 ===
total_end_time = time.time()
total_duration = round(total_end_time - total_start_time, 2)
print(f"全部处理完成，总耗时 {total_duration} 秒")
print(f"结果已保存至：{OUTPUT_PATH}")