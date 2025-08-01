# api/deepseek_client.py
import requests
from config import API_KEY, API_URL, MODEL_NAME

def call_deepseek(messages: list[dict]) -> str:
    """
    调用DeepSeek API，直接使用构造好的messages数组
    """
    headers = {
        "Authorization": f"Bearer {API_KEY}",
        "Content-Type": "application/json"
    }

    payload = {
        "model": MODEL_NAME,
        "messages": messages,  # 直接使用传入的messages
        "temperature": 0.3,
        "top_p": 0.9,
        "max_tokens": 2048
    }

    response = requests.post(API_URL, headers=headers, json=payload)
    response.raise_for_status()
    return response.json()["choices"][0]["message"]["content"]