# main.py
import pandas as pd
import time
import os
from data.input_loader import load_field_dict, load_appendix_data
from prompts.logic_prompt_builder import load_few_shot_examples, build_few_shot_block, build_prompt
from api.deepseek_client import call_deepseek

# === 开始计时 ===
total_start_time = time.time()

# === 配置项 ===
EXCEL_PATH = "examples/一阶段：变量示例_k1.1_20250728.xlsx"
OUTPUT_PATH = "output/变量逻辑生成结果.xlsx"
DICT_PATH = "examples/CC16_二征征信衍生变量库输入数据字典.xlsx"
FEW_SHOT_PATH = "examples/一阶段：变量示例_k1.0_20250722.xlsx"
SHEET_NAME = "算话变量"
START_ROW = 0     # 起始处理行，可指定从第几行开始恢复
MAX_ROWS = None   # 可限制处理行数，如 10 条测试

# === 加载数据 ===
full_df = pd.read_excel(EXCEL_PATH, sheet_name=SHEET_NAME)
field_dict = load_field_dict(DICT_PATH, sheet="征信合表")
appendix_data = load_appendix_data(DICT_PATH, sheet="附录")
few_shot_df = load_few_shot_examples(FEW_SHOT_PATH)
few_shot_block = build_few_shot_block(few_shot_df)
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
        prompt = build_prompt(var_name, java_code, field_dict, appendix_data, few_shot_block)
        result = call_deepseek(prompt)
        end_time = time.time()
        duration = round(end_time - start_time, 2)
        print(f"第 {i + 1} 行处理完成，用时 {duration} 秒")
        print(f"运行结果：{result}")
        print()
        # 将结果写入 DataFrame
        full_df.at[i, "取值逻辑-模型"] = result
        full_df.to_excel(OUTPUT_PATH, index=False)
        time.sleep(2)  # 避免请求过快，调整频率
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
