# prompts/logic_prompt_builder.py
import pandas as pd

def load_few_shot_examples(excel_path: str, sheet: str = "算话变量", max_examples: int = 4):
    df = pd.read_excel(excel_path, sheet_name=sheet)
    df = df[["中文名", "代码", "取值逻辑-修订"]].dropna()
    return df.head(max_examples)

def build_few_shot_block(few_shot_df: pd.DataFrame) -> str:
    few_shot_block = ""
    for i, row in few_shot_df.iterrows():
        few_shot_block += f"""
        【示例{i+1}】
        变量名：{row['中文名']}
        Java代码：
        ```java
        {row['代码']}
        ```

        输出：
        {row['取值逻辑-修订']}
        """
    return few_shot_block.strip()

def build_prompt(variable_name: str, java_code: str, field_dict_df: pd.DataFrame, appendix_data_df: pd.DataFrame, few_shot_block: str) -> str:
    # 构造 few-shot 示例文本
    prompt = f"""

    你是一个征信建模专家。你将根据变量名称、Java 源代码以及字段字典，生成结构化、标准化的中文“取值逻辑说明”。

    ---

    【输出格式要求】请严格按照以下格式输出：
    1. 使用“字段中文名（字段XML_TAG）”格式明确说明所用字段，不要出现字段英文名或代码变量名，同样的字段中文名如果对应多个字段XML_TAG需要先确认该字段属于哪张表，再取对应的字段XML_TAG；
    2. 逻辑结构保持简明、紧凑，不要使用“方法”、“对象”、“属性”等代码术语，不要出现具体代码内容；
    2. 明确筛选条件的字段和取值规则；
    3. 明确时间范围推导方式，例如报告时间(PA01AR01)减去<Px>个月作为起始月，字段为年月格式；
    4. 明确统计字段与其取值编码（如逾期状态代码为1-7、G、B、D、Z，具体见字段参考字典）；
    5. 明确聚合粒度与方式（如每账户统计、再按SUM聚合）；
    6. 明确特殊筛选规则（如账户状态字段有优先级：优先取PD01CD01，否则取PD01BD01）；
    7. 不允许使用模糊术语（如“逾期状态”、“币种匹配”等），必须使用征信标准字段与值。
    8. 结果最多4步，逻辑必须覆盖代码中的筛选、时间、聚合操作，不要重复描述，不要总结性语言。

    ---
    
    你还可以参考如下【示例逻辑】：

    {few_shot_block}

    【待生成变量】
    变量名：{variable_name}
    Java代码：

    ```java
    {java_code}
    ```

    字段字典如下（供参考）：
    {field_dict_df.to_markdown(index=False)}
    
    字段参考字典如下（供模型参考编码与字段含义）：
    {appendix_data_df.to_markdown(index=False)}
    """
    return prompt

