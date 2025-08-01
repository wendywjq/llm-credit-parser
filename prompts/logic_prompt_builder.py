# prompts/logic_prompt_builder.py
import pandas as pd
import sys
from pathlib import Path
sys.path.append(str(Path(__file__).parent.parent))
from config import CORE_REQUIREMENTS

def load_few_shot_examples(excel_path: str, sheet: str = "算话变量", max_examples: int = 4) -> list[dict]:
    """
    从Excel加载few-shot示例，返回user→assistant格式的消息列表
    """
    df = pd.read_excel(excel_path, sheet_name=sheet)
    df = df[["中文名", "代码", "取值逻辑-修订"]].dropna()
    messages = []
    
    for _, row in df.head(max_examples).iterrows():
        messages.append({
            "role": "user",
            "content": f"""{CORE_REQUIREMENTS}
            变量名：{row['中文名']}
            Java代码：
            ```java
            {row['代码']}
            ```
            """
        })
        messages.append({
            "role": "assistant",
            "content": row['取值逻辑-修订']
        })
    
    return messages

def build_messages(variable_name: str, java_code: str, field_dict_df: pd.DataFrame, 
                  appendix_data_df: pd.DataFrame, few_shot_messages: list[dict]) -> list[dict]:
    """
    构造完整的messages数组，包含：
    1. 系统指令
    2. few-shot示例(user→assistant)
    3. 当前待处理的代码(user)
    """
    # 系统消息
    system_message = {
        "role": "system",
        "content": CORE_REQUIREMENTS
    }
    
    # 构造字段参考信息
    field_reference = f"""
    字段字典参考：
    {field_dict_df.to_markdown(index=False)}
    
    字段编码参考附录：
    {appendix_data_df.to_markdown(index=False)}
    """
    
    # 当前待处理的代码
    current_user_message = {
        "role": "user",
        "content": f"""{CORE_REQUIREMENTS}
        变量名：{variable_name}
        Java代码：
        ```java
        {java_code}
        ```
        {field_reference}
        """
    }
    
    return [system_message] + few_shot_messages + [current_user_message]