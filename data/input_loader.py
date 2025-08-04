# data/input_loader.py
import pandas as pd

def load_field_dict(path, sheet="征信合表"):
    df = pd.read_excel(path, sheet_name=sheet)
    df = df[["字段XML_TAG", "字段中文名", "表中文名", "表TAG", "字段描述"]].dropna()
    df = df.drop_duplicates(subset=["字段XML_TAG"])
    return df.reset_index(drop=True)

def load_appendix_data(path, sheet="附录"):
    df = pd.read_excel(path, sheet_name=sheet)
    df["代码表"] = df["代码表"].ffill()  # 填充代码表列的空值
    df = df[["代码表", "代码", "中文名称"]].dropna()
    return df.reset_index(drop=True)

def load_java_code(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        return f.read()
