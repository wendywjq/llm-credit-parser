# api/deepseek_client.py
import requests
import time
from config import API_KEY, API_URL, MODEL_NAME

def call_deepseek(prompt: str, max_retries: int = 5, retry_delay: int = 10) -> str:
    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": MODEL_NAME,
        "messages": [
            {"role": "system", "content": """
    你是一个熟悉征信系统结构、字段规则、账单周期和变量计算逻辑的专家。你非常清楚如何基于征信报告字段，构造完整且字段一致、逻辑闭环、计算规则明确的变量取值逻辑。
    你将根据变量名称、Java 源代码以及字段字典，生成结构化、标准化的中文“取值逻辑说明”。
    【输出格式要求】请严格按照以下格式输出：
    1. 务必使用字段字典内的“字段中文名(字段XML_TAG)”格式说明所用字段，【不要出现】字段英文名或代码变量名；同样的字段中文名如果对应多个字段XML_TAG需要先确认该字段属于哪张表，再取对应的字段XML_TAG；
    2. 逻辑结构保持简明、紧凑，不要使用“方法”、“对象”、“属性”等代码术语，不要出现具体代码内容；
    3. 明确筛选条件的字段和取值规则；
    4. 明确特殊筛选规则（如账户状态字段有优先级：优先取[最近一次月度表现信息表.账户状态(PD01CD01)]，如果为空，则取[最新表现信息表.账户状态(PD01BD01))；
    5. 明确时间范围推导方式（如报告时间(PA01AR01)减去<Px>个月作为起始月，字段为年月格式）；
    6. 明确统计字段与其取值编码（如x.getStatusInt() > 0代表还款状态(PD01ED01)为[1-7,G,B,D,Z]的记录，具体见字段编码参考附录）；
    7. 明确聚合粒度与聚合方式；
    8. 不允许使用模糊术语（如“逾期状态”、“币种匹配”等），必须使用征信标准字段与值。
    9. 结果最多4步，逻辑必须覆盖代码中的筛选、时间、聚合操作，不要重复描述，不要总结性语言。
    """},
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.3,
        "top_p": 0.9,
        "max_tokens": 1024
    }

    for attempt in range(max_retries):
        try:
            response = requests.post(API_URL, headers=headers, json=payload)
            response.raise_for_status()
            return response.json()["choices"][0]["message"]["content"]
        except requests.exceptions.HTTPError as http_err:
            if response.status_code == 429:
                wait_time = retry_delay * (2 ** attempt)
                print(f"[警告] 第 {attempt+1} 次请求遇到 429 Too Many Requests，{wait_time} 秒后重试...")
                time.sleep(wait_time)
            else:
                print(f"[错误] HTTP 错误：{http_err}")
                raise
        except Exception as err:
            print(f"[错误] 其他异常：{err}")
            raise
    raise RuntimeError("达到最大重试次数后仍然失败。")
