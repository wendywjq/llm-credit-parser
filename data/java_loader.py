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
