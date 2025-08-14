# utils/header_used_bodies.py
# ----------------------------------------------------------------------
# 针对“头文件注入”生成一个紧凑的 Markdown 代码块，用于 LLM Prompt：
# - 自动识别当前 java_code 使用到的 ParameterMapping.* / Utils.* 方法
# - 对 ParameterMapping：注入“使用到的方法体”（可选去注释/空行），确保业务口径清晰
# - 对 Utils：仅注入“使用到的方法签名”，极省 token，避免大括号/try 骨架噪声
# - 同时尽量注入常见枚举（LoanBusinessType, StatType, CalcAcctType, CurrencyType）
# - 最大字符数 max_chars 防止超长
# ----------------------------------------------------------------------

from __future__ import annotations
import os
import re
from typing import Dict, List, Iterable, Optional

__all__ = [
    "build_used_methods_header_block",
    "strip_comments_and_blanklines",
    "find_method_body",
    "find_enum_block",
]

# 可复用：去注释与空行
def strip_comments_and_blanklines(code: str) -> str:
    out = []
    i, n = 0, len(code)
    in_line_cmt = in_block_cmt = in_str = in_char = False
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

    lines = "".join(out).splitlines()
    non_empty = [ln for ln in lines if ln.strip() != ""]
    return "\n".join(non_empty) + ("\n" if non_empty else "")


# 方法体抽取：通过方法名找到完整方法体（含注解），做花括号配对
SIGNATURE_HEAD = re.compile(
    r"^\s*(?:public|protected|private)\s+(?:static\s+|final\s+|abstract\s+|synchronized\s+|native\s+|strictfp\s+)*"
)

def find_method_body(src: str, method_name: str) -> Optional[str]:
    it = [m.start() for m in re.finditer(r"\b%s\s*\(" % re.escape(method_name), src)]
    for pos in it:
        line_start = src.rfind("\n", 0, pos) + 1
        # 向上吸纳注解
        scan_start = line_start
        for _ in range(6):
            prev_nl = src.rfind("\n", 0, scan_start - 1)
            if prev_nl == -1:
                break
            prev_line = src[prev_nl + 1: scan_start]
            if prev_line.strip().startswith("@"):
                scan_start = prev_nl + 1
                continue
            break

        sig_block = src[scan_start: pos]
        if not SIGNATURE_HEAD.search(sig_block):
            continue

        brace_pos = src.find("{", pos)
        if brace_pos == -1:
            continue

        depth = 0
        i = brace_pos
        n = len(src)
        in_str = in_char = False
        escape = False
        while i < n:
            ch = src[i]
            if in_str:
                if not escape and ch == '"':
                    in_str = False
                escape = (ch == "\\" and not escape)
                i += 1
                continue
            if in_char:
                if not escape and ch == "'":
                    in_char = False
                escape = (ch == "\\" and not escape)
                i += 1
                continue
            if ch == '"':
                in_str = True
                i += 1
                escape = False
                continue
            if ch == "'":
                in_char = True
                i += 1
                escape = False
                continue

            if ch == "{":
                depth += 1
            elif ch == "}":
                depth -= 1
                if depth == 0:
                    return src[scan_start: i + 1]
            i += 1
    return None


# 枚举块抽取
def find_enum_block(src: str, enum_name: str) -> Optional[str]:
    m = re.search(r"\benum\s+%s\b" % re.escape(enum_name), src)
    if not m:
        return None
    brace_pos = src.find("{", m.end())
    if brace_pos == -1:
        return None
    line_start = src.rfind("\n", 0, m.start()) + 1

    depth = 0
    i = brace_pos
    n = len(src)
    in_str = in_char = False
    escape = False
    while i < n:
        ch = src[i]
        if in_str:
            if not escape and ch == '"':
                in_str = False
            escape = (ch == "\\" and not escape)
            i += 1
            continue
        if in_char:
            if not escape and ch == "'":
                in_char = False
            escape = (ch == "\\" and not escape)
            i += 1
            continue

        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return src[line_start: i + 1]
        i += 1
    return None


# —— 仅签名模式（ Utils 用 ）：从源码提取包行与类头 + 已用方法签名
_CLASS_DECL_RE = re.compile(r"^\s*public\s+(?:final\s+)?class\s+([A-Za-z_]\w*)\b")

def _extract_package_and_class_header(src: str, target_class: str) -> tuple[str, str]:
    package_line = ""
    class_header = f"public final class {target_class} {{"
    for ln in src.splitlines():
        s = ln.strip()
        if s.startswith("package ") and s.endswith(";"):
            package_line = ln
        m = _CLASS_DECL_RE.match(ln)
        if m and target_class == m.group(1):
            # 使用源码真实类声明，确保风格一致
            # 补足 '{'，避免产生多余层级
            ch = ln.rstrip()
            if not ch.endswith("{"):
                ch = ch + " {"
            class_header = ch
    return package_line, class_header

_METHOD_DEF_RE = re.compile(
    r"^\s*(public|protected|private)\s+(?:static\s+|final\s+|abstract\s+|synchronized\s+|native\s+|strictfp\s+)*"
    r"([A-Za-z_][\w\<\>\[\]\.?& ]+\s+)?"
    r"([A-Za-z_]\w*)\s*\(([^)]*)\)"
)

def _make_signatures_for_methods(src: str, used: set[str]) -> list[str]:
    out = []
    for ln in src.splitlines():
        m = _METHOD_DEF_RE.match(ln)
        if not m:
            continue
        ret_type = (m.group(2) or "").strip()
        name = m.group(3)
        params = m.group(4).strip()
        if name not in used:
            continue
        if ret_type:
            out.append(f"{ret_type} {name}({params});")
        else:
            out.append(f"{name}({params});")
    return out


# ========== 新增：Class 块抽取 ==========
def find_class_block(src: str, class_name: str) -> Optional[str]:
    """
    抽取顶层 class 块：从 'class Xxx' 到配对 '}'。
    """
    m = re.search(r"\bclass\s+%s\b" % re.escape(class_name), src)
    if not m:
        return None
    # 找到 '{'
    brace_pos = src.find("{", m.end())
    if brace_pos == -1:
        return None
    # 从类声明行的起始位置截取
    line_start = src.rfind("\n", 0, m.start()) + 1

    depth = 0
    i = brace_pos
    n = len(src)
    in_str = in_char = False
    escape = False
    while i < n:
        ch = src[i]
        if in_str:
            if not escape and ch == '"':
                in_str = False
            escape = (ch == "\\" and not escape)
            i += 1
            continue
        if in_char:
            if not escape and ch == "'":
                in_char = False
            escape = (ch == "\\" and not escape)
            i += 1
            continue

        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                return src[line_start: i + 1]
        i += 1
    return None

# ========== 新增：识别 java_code 中“用到的 params 类型名” ==========
PARAM_IMPORT_RE = re.compile(
    r"\bimport\s+com\.shuoen\.varshow\.shvar\.vars\.params\.([A-Za-z_]\w*)\s*;"
)
PARAM_FQN_RE = re.compile(
    r"\bcom\.shuoen\.varshow\.shvar\.vars\.params\.([A-Za-z_]\w*)\b"
)
PARAM_TOKEN_DOT_RE = re.compile(
    r"\b([A-Z][A-Za-z0-9_]*)\s*\."  # 捕捉形如 CalcAcctType.Loan
)

def detect_used_param_types(java_code: str, param_sources: Dict[str, str]) -> List[str]:
    """
    返回当前 java_code 用到的 params 类型名（与 param_sources 的 key 取交集）
    检测来源：
      1) import com.shuoen.varshow.shvar.vars.params.Xxx;
      2) 全限定名 com.shuoen.varshow.shvar.vars.params.Xxx
      3) 符号用法 Xxx.SOMETHING（以点号跟随的大写驼峰）
    """
    used = set(PARAM_IMPORT_RE.findall(java_code))
    used.update(PARAM_FQN_RE.findall(java_code))

    # 扫描点号用法（避免误报，仅保留 param_sources 里存在的）
    token_candidates = set(PARAM_TOKEN_DOT_RE.findall(java_code))
    used.update({name for name in token_candidates if name in param_sources})

    # 与已加载的 param 源码做交集
    return [name for name in used if name in param_sources]


# —— 当前 java_code 中使用的方法识别
PARAM_CALL_RE = re.compile(r"\bParameterMapping\.(\w+)\s*\(")
UTILS_CALL_RE = re.compile(r"\bUtils\.(\w+)\s*\(")


def build_used_methods_header_block(
    java_code: str,
    headers: Dict[str, str],                # {filename: source_code}
    enum_candidates: Iterable[str] = ("LoanBusinessType", "StatType", "CalcAcctType", "CurrencyType"),
    strip_comments: bool = True,
    max_chars: int = 12000,
    class_policy: Dict[str, str] | None = None,
    param_sources: Dict[str, str] | None = None,
) -> str:
    """
    仅注入“当前 java_code 实际调用的方法”的头文件内容：
      - ParameterMapping: 注入方法体（去注释/空行可选）
      - Utils: 注入方法签名（不含方法体）
      - 注入常见枚举块（若能定位）
    class_policy:
      - "bodies"      -> 注入方法体
      - "signatures"  -> 注入方法签名
      - 未指定类按 "bodies" 处理
    """
    if class_policy is None:
        class_policy = {"ParameterMapping": "bodies", "Utils": "signatures"}
    if param_sources is None:
        param_sources = {}

    used_param = set(PARAM_CALL_RE.findall(java_code))
    used_utils = set(UTILS_CALL_RE.findall(java_code))

    # 归类头文件源码（按文件名推断类）
    logical_sources: Dict[str, str] = {}
    for fname, src in headers.items():
        base = os.path.basename(fname)
        if "ParameterMapping" in base:
            logical_sources["ParameterMapping"] = src
        elif "Utils" in base:
            logical_sources["Utils"] = src
        else:
            logical_sources[base.split(".")[0]] = src

    parts: List[str] = []
    total = 0
    
    # ① 先注入 params/ 中“用到的类型”的 enum/class 定义（体量小、价值高）
    used_param_types = detect_used_param_types(java_code, param_sources)
    for type_name in used_param_types:
        src = param_sources.get(type_name, "")
        if not src:
            continue
        block = find_enum_block(src, type_name)
        if not block:
            block = find_class_block(src, type_name)  # 兜底：如果不是 enum，也尝试 class
        if not block:
            continue
        text = strip_comments_and_blanklines(block) if strip_comments else block
        chunk = f"// params.{type_name}\n```java\n{text}\n```\n"
        if total + len(chunk) <= max_chars:
            parts.append(chunk)
            total += len(chunk)
            print("param", chunk)
        else:
            break  # 达到长度上限，提前结束

    # ② 注入常用枚举（仍保留原来在普通头文件中的枚举寻找逻辑）
    for enum_name in enum_candidates:
        # 如果该枚举已经在 params 里注入过，就无需再从 headers 里找
        if enum_name in used_param_types:
            continue
        for src in logical_sources.values():
            enum_block = find_enum_block(src, enum_name)
            if enum_block:
                text = strip_comments_and_blanklines(enum_block) if strip_comments else enum_block
                chunk = f"// enum {enum_name}\n```java\n{text}\n```\n"
                if total + len(chunk) <= max_chars:
                    parts.append(chunk)
                    total += len(chunk)
                break

    # ③ ParameterMapping：按策略注入（方法体或签名）
    if used_param and "ParameterMapping" in logical_sources:
        src = logical_sources["ParameterMapping"]
        policy = class_policy.get("ParameterMapping", "bodies")
        if policy == "signatures":
            # 如果你想对 ParameterMapping 也仅保留签名，可改用签名抽取
            from utils.java_minify import extract_java_signatures
            sigs = extract_java_signatures(src)
            # 仅保留已用方法名
            filtered_lines = []
            for ln in sigs.splitlines():
                s = ln.strip()
                m = re.search(r"\b([A-Za-z_]\w*)\s*\(", s)
                if m and m.group(1) in used_param:
                    filtered_lines.append(ln)
                elif s.startswith("package ") or (" class " in s or " interface " in s or " enum " in s) and s.startswith("public"):
                    filtered_lines.append(ln)
            chunk = f"// ParameterMapping (signatures)\n```java\n" + "\n".join(filtered_lines) + "\n```\n"
            if total + len(chunk) <= max_chars:
                parts.append(chunk)
                total += len(chunk)
                # print("para1", chunk)
        else:
            # bodies：注入方法体（常用于映射/判断逻辑）
            for name in sorted(used_param):
                mb = find_method_body(src, name)
                if not mb:
                    continue
                text = strip_comments_and_blanklines(mb) if strip_comments else mb
                chunk = f"// ParameterMapping.{name}\n```java\n{text}\n```\n"
                if total + len(chunk) <= max_chars:
                    parts.append(chunk)
                    total += len(chunk)
                    # print("para2", chunk)
                else:
                    break

    # ④ Utils：仅签名
    if used_utils and "Utils" in logical_sources:
        src = logical_sources["Utils"]
        pkg, class_header = _extract_package_and_class_header(src, target_class="Utils")
        sig_lines = _make_signatures_for_methods(src, used_utils)

        block_lines = []
        if pkg:
            block_lines.append(pkg)
        block_lines.append(class_header)
        for sig in sig_lines:
            block_lines.append(f"    {sig}")
        block_lines.append("}")

        chunk = "// Utils (signatures)\n```java\n" + "\n".join(block_lines) + "\n```\n"
        if total + len(chunk) <= max_chars:
            parts.append(chunk)
            total += len(chunk)
            # print("utils", chunk)
    print(f"[info] Injected {len(parts)} header parts, total {total} chars")
    print("header parts:", [p.splitlines()[0] for p in parts])

    return "".join(parts).strip() if parts else "(no used headers)"
