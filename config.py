# config.py
API_KEY = "your_api_key_here"
API_URL = "https://api.siliconflow.cn/v1/chat/completions"
MODEL_NAME = "Qwen/Qwen3-8B"

EXCEL_PATH = "examples/一阶段：变量示例_k1.1_20250728.xlsx"
OUTPUT_PATH = "output/0804变量逻辑生成结果_9_去除聚合.xlsx"
DICT_PATH = "examples/CC16_二征征信衍生变量库输入数据字典.xlsx"
FEW_SHOT_PATH = EXCEL_PATH
SHEET_NAME = "算话变量"
START_ROW = 0        # 起始行索引
MAX_ROWS = None      # 处理最大行数，可为 None 表示全部处理
APPENDIX_SHEET = "附录"
DICT_SHEET = "征信合表"

# === LLM 请求控制参数 ===
MAX_RETRIES = 5            # 最大重试次数
RETRY_BASE_DELAY = 10      # 首次重试等待时间（秒），后续乘以 2^n
REQUEST_INTERVAL = 10       # 每个请求之间的等待时间（秒），用于限速