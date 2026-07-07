package com.serliunx.stc4j.util;

import java.util.Iterator;

/**
 * 字符串工具类，提供常用的字符串判空、截取、填充、拼接等操作。
 * <p>
 * 所有方法均为 null-safe，传入 {@code null} 不会抛出 {@link NullPointerException}。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/7/7
 */
public final class StringUtils {

    private StringUtils() {
    }

    // ==================== 判空 ====================

    /**
     * 判断字符序列是否为 {@code null} 或长度为 0。
     *
     * @param cs 待判定的字符序列
     * @return 如果为 null 或长度为 0 返回 true，否则返回 false
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符序列是否不为 {@code null} 且长度大于 0。
     *
     * @param cs 待判定的字符序列
     * @return 如果不为 null 且长度大于 0 返回 true
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 判断字符序列是否为 {@code null}、长度为 0 或仅包含空白字符。
     *
     * @param cs 待判定的字符序列
     * @return 如果为 null、空串或仅空白字符返回 true
     */
    public static boolean isBlank(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return true;
        }
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符序列是否包含至少一个非空白字符。
     *
     * @param cs 待判定的字符序列
     * @return 如果包含至少一个非空白字符返回 true
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    // ==================== null-safe 转换 ====================

    /**
     * 去除两端空白后，若为空串则返回 {@code null}，否则返回去除空白后的字符串。
     *
     * @param str 待处理的字符串
     * @return 去除空白后的字符串，或 null
     */
    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 去除两端空白后，若为 {@code null} 则返回空串，否则返回去除空白后的字符串。
     *
     * @param str 待处理的字符串
     * @return 去除空白后的字符串，或空串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 若字符串为 {@code null} 则返回默认值。
     *
     * @param str          待处理的字符串
     * @param defaultStr   默认值
     * @return 原字符串或默认值
     */
    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 若字符串为 {@code null} 则返回空串。
     *
     * @param str 待处理的字符串
     * @return 原字符串或空串
     */
    public static String defaultString(String str) {
        return defaultString(str, "");
    }

    /**
     * 若字符串为 {@code null} 或空串则返回默认值。
     *
     * @param str          待处理的字符串
     * @param defaultStr   默认值
     * @return 原字符串或默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 若字符串为 {@code null}、空串或仅空白字符则返回默认值。
     *
     * @param str          待处理的字符串
     * @param defaultStr   默认值
     * @return 原字符串或默认值
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    // ==================== 拼接 ====================

    /**
     * 使用分隔符将可迭代对象中的元素拼接为字符串。
     *
     * @param iterable  元素集合
     * @param separator 分隔符，可为 null
     * @return 拼接后的字符串
     */
    public static String join(Iterable<?> iterable, String separator) {
        if (iterable == null) {
            return "";
        }
        String sep = separator == null ? "" : separator;
        StringBuilder sb = new StringBuilder();
        Iterator<?> iterator = iterable.iterator();
        boolean first = true;
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null) {
                if (!first) {
                    sb.append(sep);
                }
                sb.append(obj);
                first = false;
            }
        }
        return sb.toString();
    }

    /**
     * 使用分隔符将数组中的元素拼接为字符串。
     *
     * @param array     元素数组
     * @param separator 分隔符，可为 null
     * @param <T>       元素类型
     * @return 拼接后的字符串
     */
    public static <T> String joinArray(T[] array, String separator) {
        if (array == null) {
            return "";
        }
        String sep = separator == null ? "" : separator;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null) {
                if (!first) {
                    sb.append(sep);
                }
                sb.append(array[i]);
                first = false;
            }
        }
        return sb.toString();
    }

    // ==================== 拆分 ====================

    /**
     * 使用正则表达式拆分字符串，null-safe。
     *
     * @param str   待拆分的字符串
     * @param regex 正则表达式分隔符
     * @return 拆分后的数组，若输入为 null 则返回空数组
     */
    public static String[] split(String str, String regex) {
        if (str == null) {
            return new String[0];
        }
        return str.split(regex);
    }

    // ==================== 截取 ====================

    /**
     * 返回分隔符之前的部分。
     *
     * @param str       原字符串
     * @param separator 分隔符
     * @return 分隔符之前的子串，未找到分隔符或输入为 null 返回空串
     */
    public static String substringBefore(String str, String separator) {
        if (str == null || separator == null) {
            return "";
        }
        int index = str.indexOf(separator);
        return index == -1 ? "" : str.substring(0, index);
    }

    /**
     * 返回分隔符之后的部分。
     *
     * @param str       原字符串
     * @param separator 分隔符
     * @return 分隔符之后的子串，未找到分隔符或输入为 null 返回空串
     */
    public static String substringAfter(String str, String separator) {
        if (str == null || separator == null) {
            return "";
        }
        int index = str.indexOf(separator);
        return index == -1 ? "" : str.substring(index + separator.length());
    }

    /**
     * 返回两个分隔符之间的子串。
     *
     * @param str   原字符串
     * @param open  起始分隔符
     * @param close 结束分隔符
     * @return 两个分隔符之间的子串，未找到返回 null
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start == -1) {
            return null;
        }
        int end = str.indexOf(close, start + open.length());
        if (end == -1) {
            return null;
        }
        return str.substring(start + open.length(), end);
    }

    // ==================== 前缀/后缀操作 ====================

    /**
     * 如果字符串以指定前缀开头，则去掉该前缀。
     *
     * @param str    原字符串
     * @param remove 要移除的前缀
     * @return 去掉前缀后的字符串
     */
    public static String removeStart(String str, String remove) {
        if (str == null || remove == null) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * 如果字符串以指定后缀结尾，则去掉该后缀。
     *
     * @param str    原字符串
     * @param remove 要移除的后缀
     * @return 去掉后缀后的字符串
     */
    public static String removeEnd(String str, String remove) {
        if (str == null || remove == null) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    // ==================== 大小写 ====================

    /**
     * 将首字母转为大写。
     *
     * @param str 原字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char first = str.charAt(0);
        char upper = Character.toUpperCase(first);
        if (first == upper) {
            return str;
        }
        return upper + str.substring(1);
    }

    /**
     * 将首字母转为小写。
     *
     * @param str 原字符串
     * @return 首字母小写后的字符串
     */
    public static String uncapitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        char first = str.charAt(0);
        char lower = Character.toLowerCase(first);
        if (first == lower) {
            return str;
        }
        return lower + str.substring(1);
    }

    /**
     * 翻转字符串的大小写。
     *
     * @param str 原字符串
     * @return 翻转大小写后的字符串
     */
    public static String swapCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ==================== 比较 ====================

    /**
     * 忽略大小写比较两个字符序列是否相等。
     *
     * @param cs1 字符序列 1
     * @param cs2 字符序列 2
     * @return 两者忽略大小写相等返回 true
     */
    public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        for (int i = 0; i < cs1.length(); i++) {
            char c1 = cs1.charAt(i);
            char c2 = cs2.charAt(i);
            if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 忽略大小写判断源字符序列中是否包含目标字符序列。
     *
     * @param source 源字符序列
     * @param target 目标字符序列
     * @return 忽略大小写包含时返回 true
     */
    public static boolean containsIgnoreCase(CharSequence source, CharSequence target) {
        if (source == null || target == null) {
            return false;
        }
        if (target.length() == 0) {
            return true;
        }
        String src = source.toString().toLowerCase();
        String tgt = target.toString().toLowerCase();
        return src.contains(tgt);
    }

    // ==================== 替换 ====================

    /**
     * 替换字符串中所有出现的目标子串。
     *
     * @param text         原字符串
     * @param searchString 被替换的子串
     * @param replacement  替换为的子串
     * @return 替换后的字符串
     */
    public static String replace(String text, String searchString, String replacement) {
        if (text == null || searchString == null || replacement == null) {
            return text;
        }
        if (searchString.isEmpty()) {
            return text;
        }
        return text.replace(searchString, replacement);
    }

    // ==================== 填充与补位 ====================

    /**
     * 在字符串左侧填充指定字符至指定长度。
     *
     * @param str    原字符串
     * @param size   目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return repeat(String.valueOf(padChar), size);
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 在字符串右侧填充指定字符至指定长度。
     *
     * @param str    原字符串
     * @param size   目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return repeat(String.valueOf(padChar), size);
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(size);
        sb.append(str);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    // ==================== 其他 ====================

    /**
     * 反转字符串。
     *
     * @param str 原字符串
     * @return 反转后的字符串
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 重复拼接字符串指定次数。
     *
     * @param str   原字符串
     * @param count 重复次数
     * @return 重复拼接后的字符串
     */
    public static String repeat(String str, int count) {
        if (str == null) {
            return null;
        }
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 将字符串截断至指定长度，超出部分用 {@code "..."} 替代。
     *
     * @param str       原字符串
     * @param maxLength 最大长度（含省略号）
     * @return 截断后的字符串
     */
    public static String abbreviate(String str, int maxLength) {
        return abbreviate(str, 0, maxLength);
    }

    /**
     * 在指定偏移处开始，将字符串截断至指定长度，超出部分用 {@code "..."} 替代。
     *
     * @param str       原字符串
     * @param offset    起始偏移
     * @param maxLength 最大长度（含省略号）
     * @return 截断后的字符串
     */
    public static String abbreviate(String str, int offset, int maxLength) {
        if (str == null) {
            return null;
        }
        if (maxLength < 4) {
            throw new IllegalArgumentException("maxLength must be at least 4");
        }
        if (str.length() <= maxLength) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxLength - 3) {
            offset = str.length() - (maxLength - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxLength - 3) + "...";
        }
        return "..." + abbreviate(str.substring(offset), maxLength - 3);
    }

    /**
     * 返回字符序列的长度，null-safe。
     *
     * @param cs 字符序列
     * @return 长度，null 时返回 0
     */
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
