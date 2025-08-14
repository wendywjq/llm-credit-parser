import os

def get_all_java_files(base_path: str = "vars") -> list[dict]:
    """
    递归读取 base_path 目录下所有 Java 文件，返回文件信息字典列表。
    每个字典包含：
    - file_path：文件完整路径
    - file_name：文件名
    - java_code：Java 源代码内容
    """
    java_files = []
    for root, _, files in os.walk(base_path):
        for file in files:
            if file.endswith(".java"):
                full_path = os.path.join(root, file)
                try:
                    with open(full_path, "r", encoding="utf-8") as f:
                        java_files.append({
                            "file_path": full_path,
                            "file_name": file,
                            "java_code": f.read()
                        })
                except Exception as e:
                    print(f"无法读取文件 {full_path}：{e}")
    return java_files

def load_header_files(paths: list[str]) -> dict[str, str]:
    """
    读取公共头文件，返回 {文件名: 代码文本} 的字典。
    缺失文件不报错，仅警告跳过。
    """
    out = {}
    for p in paths:
        try:
            with open(p, "r", encoding="utf-8") as f:
                out[os.path.basename(p)] = f.read()
        except Exception as e:
            print(f"[warn] header not loaded: {p} ({e})")
    return out

def load_param_sources(params_dir: str) -> dict[str, str]:
    """
    读取 params_dir 下所有 .java 文件，返回 {类名: 源码文本}。
    例如：{"CalcAcctType": "...", "LoanBusinessType": "..."}
    """
    out = {}
    if not params_dir or not os.path.isdir(params_dir):
        return out
    for root, _, files in os.walk(params_dir):
        for fn in files:
            if not fn.endswith(".java"):
                continue
            path = os.path.join(root, fn)
            class_name = os.path.splitext(fn)[0]
            try:
                with open(path, "r", encoding="utf-8") as f:
                    out[class_name] = f.read()
            except Exception as e:
                print(f"[warn] failed to load param file {path}: {e}")
    return out