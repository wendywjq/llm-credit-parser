# config.py
API_KEY = "your_api_key_here"
API_URL = "https://api.siliconflow.cn/v1/chat/completions"
MODEL_NAME = "deepseek-ai/DeepSeek-R1-0528-Qwen3-8B"

EXCEL_PATH = "examples/一阶段：变量示例_k1.1_20250728.xlsx"
OUTPUT_PATH = "output/0731变量逻辑生成结果_1.xlsx"
DICT_PATH = "examples/CC16_二征征信衍生变量库输入数据字典.xlsx"
FEW_SHOT_PATH = EXCEL_PATH
SHEET_NAME = "算话变量"
START_ROW = 0        # 起始行索引
MAX_ROWS = None      # 处理最大行数，可为 None 表示全部处理
APPENDIX_SHEET = "附录"
DICT_SHEET = "征信合表"
