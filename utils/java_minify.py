# utils/java_minify.py
# ----------------------------------------------------------------------
# 提供两种精简策略：
# 1) strip_java_comments_and_blanklines: 去掉注释与空行，保留方法体（更接近真实实现）
# 2) extract_java_signatures: 仅保留 package / 类/接口/枚举声明 / 方法签名（不含方法体）
# ----------------------------------------------------------------------

from __future__ import annotations
import re

__all__ = [
    "strip_java_comments_and_blanklines",
    "extract_java_signatures",
]

# 基础匹配
PACKAGE_LINE = re.compile(r"^\s*package\s+[\w\.]+;\s*$")
IMPORT_LINE = re.compile(r"^\s*import\s+[\w\.\*]+;\s*$")
ANNOTATION_LINE = re.compile(r"^\s*@[\w\.\(\)=, \t]+$")

# 类/接口/枚举开头（包含花括号）
CLASS_HEADER = re.compile(
    r"^\s*(?:public|protected|private)?\s*(?:final|abstract|static)?\s*"
    r"(class|interface|enum)\s+[A-Za-z_]\w*[^;{]*\{"
)

# 方法（带方法体的声明）
METHOD_WITH_BODY = re.compile(
    r"^\s*(?:public|protected|private)\s+(?:static\s+|final\s+|abstract\s+|synchronized\s+|native\s+|strictfp\s+)*"
    r"([A-Za-z_][\w\<\>\[\]\.?& ]+\s+)?"
    r"([A-Za-z_]\w*)\s*\(([^)]*)\)\s*(?:throws\s+[^{;]+)?\s*\{"
)

# 方法（无方法体；接口内或抽象方法以 ; 结尾）
METHOD_NO_BODY = re.compile(
    r"^\s*(?:public|protected|private)?\s*(?:static\s+|final\s+|abstract\s+|synchronized\s+|native\s+|strictfp\s+)*"
    r"([A-Za-z_][\w\<\>\[\]\.?& ]+\s+)"
    r"([A-Za-z_]\w*)\s*\(([^)]*)\)\s*(?:throws\s+[^;]+)?\s*;"
)


def strip_java_comments_and_blanklines(code: str) -> str:
    """
    移除 //、/* */、/** */ 注释与空行，保留方法体和有效语句。
    使用状态机，避免误删字符串/字符常量中的注释字样。
    """
    out = []
    i, n = 0, len(code)
    in_line_cmt = False
    in_block_cmt = False
    in_str = False
    in_char = False
    escape = False

    while i < n:
        c = code[i]
        nxt = code[i + 1] if i + 1 < n else ""

        if in_line_cmt:
            if c == "\n":
                in_line_cmt = False
                out.append(c)
            i += 1
            continue

        if in_block_cmt:
            if c == "*" and nxt == "/":
                in_block_cmt = False
                i += 2
            else:
                i += 1
            continue

        if in_str:
            out.append(c)
            if not escape and c == '"':
                in_str = False
            escape = (c == "\\" and not escape)
            i += 1
            continue

        if in_char:
            out.append(c)
            if not escape and c == "'":
                in_char = False
            escape = (c == "\\" and not escape)
            i += 1
            continue

        # 进入注释或字面量
        if c == "/" and nxt == "/":
            in_line_cmt = True
            i += 2
            continue
        if c == "/" and nxt == "*":
            in_block_cmt = True
            i += 2
            continue
        if c == '"':
            in_str = True
            out.append(c)
            i += 1
            escape = False
            continue
        if c == "'":
            in_char = True
            out.append(c)
            i += 1
            escape = False
            continue

        out.append(c)
        i += 1

    # 去空行
    lines = "".join(out).splitlines()
    non_empty = [ln for ln in lines if ln.strip() != ""]
    return "\n".join(non_empty) + ("\n" if non_empty else "")


def extract_java_signatures(code: str) -> str:
    """
    仅提取 package 行、类/接口/枚举声明行、方法签名（不含方法体）。
    - 方法体行会被转换成一行 'ret name(params);'
    - 适合作为“签名模式”注入到 LLM prompt 中
    """
    code_nc = strip_java_comments_and_blanklines(code)
    out_lines = []
    seen_class_header = False

    for ln in code_nc.splitlines():
        s = ln.strip()
        if not s:
            continue

        # package
        if PACKAGE_LINE.match(ln):
            out_lines.append(ln)
            continue

        # class/interface/enum header
        if CLASS_HEADER.match(ln):
            out_lines.append(ln)
            seen_class_header = True
            continue

        # annotation 行（与随后的声明行一并保留）
        if ANNOTATION_LINE.match(ln):
            out_lines.append(ln)
            continue

        # 方法（带方法体声明）-> 签名
        m1 = METHOD_WITH_BODY.match(ln)
        if m1:
            ret_type = (m1.group(1) or "").strip()
            name = m1.group(2)
            params = m1.group(3).strip()
            if ret_type:
                out_lines.append(f"{ret_type} {name}({params});")
            else:
                out_lines.append(f"{name}({params});")
            continue

        # 方法（无方法体）-> 原样保留
        m2 = METHOD_NO_BODY.match(ln)
        if m2:
            ret_type = m2.group(1).strip()
            name = m2.group(2)
            params = m2.group(3).strip()
            out_lines.append(f"{ret_type} {name}({params});")
            continue

        # 其他声明行（如 enum 内常量等）一般省略；保持签名简洁
        # 如有需要可按需扩展保留策略

    # 去重/去尾部空白
    cleaned = [x.rstrip() for x in out_lines if x.strip()]
    return "\n".join(cleaned) + ("\n" if cleaned else "")
