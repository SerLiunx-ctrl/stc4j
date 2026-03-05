package com.serliunx.stc4j.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

/**
 * {@link Assert} 的单元测试。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/5
 */
public class AssertTest {

    @Test
    public void testStateAndBooleanAssertions() {
        Assert.state(true);
        Assert.isTrue(true);
        Assert.isFalse(false);

        assertThrows(IllegalStateException.class, () -> Assert.state(false));
        assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(false));
        assertThrows(IllegalArgumentException.class, () -> Assert.isFalse(true));
    }

    @Test
    public void testNullAssertions() {
        String value = "ok";
        assertSame(value, Assert.notNull(value));
        Assert.isNull(null);

        assertThrows(IllegalArgumentException.class, () -> Assert.notNull(null));
        assertThrows(IllegalArgumentException.class, () -> Assert.isNull(value));
    }

    @Test
    public void testStringAssertions() {
        assertEquals("abc", Assert.hasLength("abc"));
        assertEquals(" abc ", Assert.hasText(" abc "));
        Assert.doesNotContain("abc", "xyz");

        assertThrows(IllegalArgumentException.class, () -> Assert.hasLength(""));
        assertThrows(IllegalArgumentException.class, () -> Assert.hasText("   "));
        assertThrows(IllegalArgumentException.class, () -> Assert.doesNotContain("abc", "b"));
    }

    @Test
    public void testArrayAndCollectionAssertions() {
        String[] array = new String[]{"a"};
        List<String> list = Arrays.asList("a", "b");
        java.util.Map<String, String> map = Collections.singletonMap("k", "v");

        assertSame(array, Assert.notEmpty(array));
        assertSame(array, Assert.noNullElements(array));
        assertSame(list, Assert.notEmpty(list));
        assertSame(list, Assert.noNullElements(list));
        assertSame(map, Assert.notEmpty(map));

        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(new String[0]));
        assertThrows(IllegalArgumentException.class, () -> Assert.noNullElements(new String[]{"a", null}));
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(Collections.emptyList()));
        assertThrows(IllegalArgumentException.class, () -> Assert.noNullElements(Arrays.asList("a", null)));
        assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(Collections.emptyMap()));
    }

    @Test
    public void testTypeAndEqualsAssertions() {
        Integer value = Assert.isInstanceOf(Integer.class, 1);
        assertEquals(Integer.valueOf(1), value);
        Assert.isAssignable(Number.class, Integer.class);
        Assert.equals("a", "a");
        Assert.notEquals("a", "b");

        assertThrows(IllegalArgumentException.class, () -> Assert.isInstanceOf(Integer.class, "1"));
        assertThrows(IllegalArgumentException.class, () -> Assert.isAssignable(Integer.class, Number.class));
        assertThrows(IllegalArgumentException.class, () -> Assert.equals("a", "b"));
        assertThrows(IllegalArgumentException.class, () -> Assert.notEquals("a", "a"));
    }
}
