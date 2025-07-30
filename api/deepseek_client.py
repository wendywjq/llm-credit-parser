# api/deepseek_client.py
import requests
from config import API_KEY, API_URL, MODEL_NAME

def call_deepseek(prompt: str) -> str:
    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": MODEL_NAME,
        "messages": [
            {"role": "system", "content": "你是一个熟悉征信系统结构、字段规则、账单周期和变量计算逻辑的专家。你非常清楚如何基于征信报告字段，构造完整且字段一致、逻辑闭环、计算规则明确的变量取值逻辑。"},
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.3,
        "top_p": 0.9,
        "max_tokens": 1024
    }

    response = requests.post(API_URL, headers=headers, json=payload)
    response.raise_for_status()
    return response.json()["choices"][0]["message"]["content"]
