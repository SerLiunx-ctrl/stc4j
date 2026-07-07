package com.serliunx.stc4j.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 字符串工具类测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/7/7
 */
public class StringUtilsTest {

    // ==================== isEmpty / isNotEmpty ====================

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("a"));
        assertFalse(StringUtils.isEmpty("  a  "));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty(" "));
        assertTrue(StringUtils.isNotEmpty("a"));
    }

    // ==================== isBlank / isNotBlank ====================

    @Test
    public void testIsBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertTrue(StringUtils.isBlank("\t\n\r"));
        assertFalse(StringUtils.isBlank(" a "));
        assertFalse(StringUtils.isBlank("abc"));
    }

    @Test
    public void testIsNotBlank() {
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank("   "));
        assertTrue(StringUtils.isNotBlank(" a "));
        assertTrue(StringUtils.isNotBlank("abc"));
    }

    // ==================== trimToNull / trimToEmpty ====================

    @Test
    public void testTrimToNull() {
        assertNull(StringUtils.trimToNull(null));
        assertNull(StringUtils.trimToNull(""));
        assertNull(StringUtils.trimToNull("   "));
        assertEquals("a", StringUtils.trimToNull(" a "));
        assertEquals("a b", StringUtils.trimToNull("  a b  "));
    }

    @Test
    public void testTrimToEmpty() {
        assertEquals("", StringUtils.trimToEmpty(null));
        assertEquals("", StringUtils.trimToEmpty(""));
        assertEquals("", StringUtils.trimToEmpty("   "));
        assertEquals("a", StringUtils.trimToEmpty(" a "));
    }

    // ==================== defaultString ====================

    @Test
    public void testDefaultString() {
        assertEquals("", StringUtils.defaultString(null));
        assertEquals("a", StringUtils.defaultString("a"));
        assertEquals("default", StringUtils.defaultString(null, "default"));
        assertEquals("a", StringUtils.defaultString("a", "default"));
    }

    // ==================== defaultIfEmpty / defaultIfBlank ====================

    @Test
    public void testDefaultIfEmpty() {
        assertEquals("default", StringUtils.defaultIfEmpty(null, "default"));
        assertEquals("default", StringUtils.defaultIfEmpty("", "default"));
        assertEquals(" ", StringUtils.defaultIfEmpty(" ", "default"));
        assertEquals("a", StringUtils.defaultIfEmpty("a", "default"));
    }

    @Test
    public void testDefaultIfBlank() {
        assertEquals("default", StringUtils.defaultIfBlank(null, "default"));
        assertEquals("default", StringUtils.defaultIfBlank("", "default"));
        assertEquals("default", StringUtils.defaultIfBlank("   ", "default"));
        assertEquals("a", StringUtils.defaultIfBlank("a", "default"));
    }

    // ==================== join ====================

    @Test
    public void testJoinIterable() {
        assertEquals("", StringUtils.join(null, ","));
        assertEquals("", StringUtils.join(new ArrayList<String>(), ","));
        assertEquals("a,b,c", StringUtils.join(Arrays.asList("a", "b", "c"), ","));
        assertEquals("abc", StringUtils.join(Arrays.asList("a", "b", "c"), null));
        assertEquals("a.b.c", StringUtils.join(Arrays.asList("a", "b", "c"), "."));
    }

    @Test
    public void testJoinArray() {
        assertEquals("", StringUtils.joinArray((String[]) null, ","));
        assertEquals("a,b,c", StringUtils.joinArray(new String[]{"a", "b", "c"}, ","));
        assertEquals("abc", StringUtils.joinArray(new String[]{"a", "b", "c"}, null));
    }

    @Test
    public void testJoinWithNullElements() {
        assertEquals("a,c", StringUtils.join(Arrays.asList("a", null, "c"), ","));
        assertEquals("a,c", StringUtils.joinArray(new String[]{"a", null, "c"}, ","));
    }

    // ==================== split ====================

    @Test
    public void testSplit() {
        assertArrayEquals(new String[0], StringUtils.split(null, ","));
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtils.split("a,b,c", ","));
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtils.split("a.b.c", "\\."));
    }

    // ==================== substringBefore / substringAfter / substringBetween ====================

    @Test
    public void testSubstringBefore() {
        assertEquals("", StringUtils.substringBefore(null, ","));
        assertEquals("", StringUtils.substringBefore("abc", null));
        assertEquals("a", StringUtils.substringBefore("a,b,c", ","));
        assertEquals("", StringUtils.substringBefore("abc", "x"));
    }

    @Test
    public void testSubstringAfter() {
        assertEquals("", StringUtils.substringAfter(null, ","));
        assertEquals("b,c", StringUtils.substringAfter("a,b,c", ","));
        assertEquals("", StringUtils.substringAfter("abc", "x"));
    }

    @Test
    public void testSubstringBetween() {
        assertNull(StringUtils.substringBetween(null, "<", ">"));
        assertNull(StringUtils.substringBetween("abc", "x", "y"));
        assertEquals("hello", StringUtils.substringBetween("<hello>", "<", ">"));
        assertEquals("name", StringUtils.substringBetween("{{name}}", "{{", "}}"));
    }

    // ==================== removeStart / removeEnd ====================

    @Test
    public void testRemoveStart() {
        assertNull(StringUtils.removeStart(null, "a"));
        assertEquals("bc", StringUtils.removeStart("abc", "a"));
        assertEquals("abc", StringUtils.removeStart("abc", "x"));
        assertEquals("abc", StringUtils.removeStart("abc", null));
    }

    @Test
    public void testRemoveEnd() {
        assertNull(StringUtils.removeEnd(null, "a"));
        assertEquals("ab", StringUtils.removeEnd("abc", "c"));
        assertEquals("abc", StringUtils.removeEnd("abc", "x"));
        assertEquals("abc", StringUtils.removeEnd("abc", null));
    }

    // ==================== capitalize / uncapitalize / swapCase ====================

    @Test
    public void testCapitalize() {
        assertNull(StringUtils.capitalize(null));
        assertEquals("", StringUtils.capitalize(""));
        assertEquals("Abc", StringUtils.capitalize("abc"));
        assertEquals("ABC", StringUtils.capitalize("ABC"));
        assertEquals("123", StringUtils.capitalize("123"));
    }

    @Test
    public void testUncapitalize() {
        assertNull(StringUtils.uncapitalize(null));
        assertEquals("", StringUtils.uncapitalize(""));
        assertEquals("aBC", StringUtils.uncapitalize("ABC"));
        assertEquals("abc", StringUtils.uncapitalize("abc"));
    }

    @Test
    public void testSwapCase() {
        assertNull(StringUtils.swapCase(null));
        assertEquals("", StringUtils.swapCase(""));
        assertEquals("aBC", StringUtils.swapCase("Abc"));
        assertEquals("ABC", StringUtils.swapCase("abc"));
        assertEquals("abc", StringUtils.swapCase("ABC"));
        assertEquals("123", StringUtils.swapCase("123"));
    }

    // ==================== equalsIgnoreCase / containsIgnoreCase ====================

    @Test
    public void testEqualsIgnoreCase() {
        assertTrue(StringUtils.equalsIgnoreCase(null, null));
        assertFalse(StringUtils.equalsIgnoreCase("a", null));
        assertFalse(StringUtils.equalsIgnoreCase(null, "a"));
        assertTrue(StringUtils.equalsIgnoreCase("abc", "ABC"));
        assertTrue(StringUtils.equalsIgnoreCase("AbC", "aBc"));
        assertFalse(StringUtils.equalsIgnoreCase("abc", "abd"));
        assertFalse(StringUtils.equalsIgnoreCase("abc", "abcd"));
    }

    @Test
    public void testContainsIgnoreCase() {
        assertFalse(StringUtils.containsIgnoreCase(null, "a"));
        assertFalse(StringUtils.containsIgnoreCase("a", null));
        assertTrue(StringUtils.containsIgnoreCase("", ""));
        assertTrue(StringUtils.containsIgnoreCase("abc", ""));
        assertTrue(StringUtils.containsIgnoreCase("HelloWorld", "low"));
        assertFalse(StringUtils.containsIgnoreCase("HelloWorld", "xyz"));
    }

    // ==================== replace ====================

    @Test
    public void testReplace() {
        assertNull(StringUtils.replace(null, "a", "b"));
        assertEquals("a", StringUtils.replace("a", null, "b"));
        assertEquals("bbc", StringUtils.replace("abc", "a", "b"));
        assertEquals("a-c", StringUtils.replace("a b c", " b ", "-"));
        assertEquals("abc", StringUtils.replace("abc", "", "x"));
    }

    // ==================== leftPad / rightPad ====================

    @Test
    public void testLeftPad() {
        assertEquals("   a", StringUtils.leftPad("a", 4, ' '));
        assertEquals("00ab", StringUtils.leftPad("ab", 4, '0'));
        assertEquals("ab", StringUtils.leftPad("ab", 2, '0'));
        assertEquals("000", StringUtils.leftPad(null, 3, '0'));
    }

    @Test
    public void testRightPad() {
        assertEquals("a   ", StringUtils.rightPad("a", 4, ' '));
        assertEquals("ab00", StringUtils.rightPad("ab", 4, '0'));
        assertEquals("ab", StringUtils.rightPad("ab", 2, '0'));
        assertEquals("000", StringUtils.rightPad(null, 3, '0'));
    }

    // ==================== reverse / repeat ====================

    @Test
    public void testReverse() {
        assertNull(StringUtils.reverse(null));
        assertEquals("", StringUtils.reverse(""));
        assertEquals("cba", StringUtils.reverse("abc"));
        assertEquals("a", StringUtils.reverse("a"));
    }

    @Test
    public void testRepeat() {
        assertNull(StringUtils.repeat(null, 3));
        assertEquals("", StringUtils.repeat("a", 0));
        assertEquals("", StringUtils.repeat("a", -1));
        assertEquals("aaa", StringUtils.repeat("a", 3));
        assertEquals("abab", StringUtils.repeat("ab", 2));
    }

    // ==================== abbreviate ====================

    @Test
    public void testAbbreviate() {
        assertNull(StringUtils.abbreviate(null, 10));
        assertEquals("abc", StringUtils.abbreviate("abc", 10));
        assertEquals("abcdefghij", StringUtils.abbreviate("abcdefghij", 10));
        assertEquals("abcdefg...", StringUtils.abbreviate("abcdefghijk", 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAbbreviateMaxLengthTooSmall() {
        StringUtils.abbreviate("abc", 3);
    }

    @Test
    public void testAbbreviateWithOffset() {
        assertEquals("...fghi...", StringUtils.abbreviate("abcdefghijklmn", 5, 10));
        assertEquals("abcdefg...", StringUtils.abbreviate("abcdefghijk", 0, 10));
    }

    // ==================== length ====================

    @Test
    public void testLength() {
        assertEquals(0, StringUtils.length(null));
        assertEquals(0, StringUtils.length(""));
        assertEquals(3, StringUtils.length("abc"));
    }

    // ==================== 组合场景 ====================

    @Test
    public void testChainedOperations() {
        String result = StringUtils.defaultIfBlank(
                StringUtils.trimToNull("   "),
                "fallback");
        assertEquals("fallback", result);

        String joined = StringUtils.join(
                Arrays.asList("a", "b", "c"),
                ", ");
        assertEquals("a, b, c", joined);
    }
}
