package com.serliunx.stc4j.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类，用于方法参数与运行状态校验。
 * <p>
 * 当断言不满足时会抛出运行时异常：
 * <ul>
 * <li>参数断言抛出 {@link IllegalArgumentException}。</li>
 * <li>状态断言抛出 {@link IllegalStateException}。</li>
 * </ul>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.3
 * @since 2026/3/5
 */
public final class Assert {

    private static final String DEFAULT_STATE_MESSAGE = "State assertion failed.";
    private static final String DEFAULT_TRUE_MESSAGE = "This expression must be true.";
    private static final String DEFAULT_FALSE_MESSAGE = "This expression must be false.";
    private static final String DEFAULT_NOT_NULL_MESSAGE = "The validated object must not be null.";
    private static final String DEFAULT_NULL_MESSAGE = "The validated object must be null.";
    private static final String DEFAULT_HAS_LENGTH_MESSAGE = "The validated string must not be null or empty.";
    private static final String DEFAULT_HAS_TEXT_MESSAGE = "The validated string must contain at least one non-whitespace character.";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_MESSAGE = "The validated array must not be null or empty.";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_MESSAGE = "The validated collection must not be null or empty.";
    private static final String DEFAULT_NOT_EMPTY_MAP_MESSAGE = "The validated map must not be null or empty.";
    private static final String DEFAULT_NO_NULL_ELEMENTS_ARRAY_MESSAGE = "The validated array must not contain null elements.";
    private static final String DEFAULT_NO_NULL_ELEMENTS_COLLECTION_MESSAGE = "The validated collection must not contain null elements.";
    private static final String DEFAULT_INSTANCE_OF_MESSAGE = "The validated object is not an instance of the required type.";
    private static final String DEFAULT_ASSIGNABLE_MESSAGE = "The validated type is not assignable to the target super type.";
    private static final String DEFAULT_EQUALS_MESSAGE = "The two values must be equal.";
    private static final String DEFAULT_NOT_EQUALS_MESSAGE = "The two values must not be equal.";
    private static final String DEFAULT_DOES_NOT_CONTAIN_MESSAGE = "The validated string must not contain the forbidden substring.";

    private Assert() {
    }

    /**
     * 校验状态表达式是否为 true。
     *
     * @param expression 状态表达式
     * @param message 断言失败时的异常信息
     * @throws IllegalStateException 当 {@code expression} 为 false 时抛出
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(resolveMessage(message, DEFAULT_STATE_MESSAGE));
        }
    }

    /**
     * 校验状态表达式是否为 true。
     *
     * @param expression 状态表达式
     * @throws IllegalStateException 当 {@code expression} 为 false 时抛出
     */
    public static void state(boolean expression) {
        state(expression, DEFAULT_STATE_MESSAGE);
    }

    /**
     * 校验表达式是否为 true。
     *
     * @param expression 待校验表达式
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当 {@code expression} 为 false 时抛出
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_TRUE_MESSAGE));
        }
    }

    /**
     * 校验表达式是否为 true。
     *
     * @param expression 待校验表达式
     * @throws IllegalArgumentException 当 {@code expression} 为 false 时抛出
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, DEFAULT_TRUE_MESSAGE);
    }

    /**
     * 校验表达式是否为 false。
     *
     * @param expression 待校验表达式
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当 {@code expression} 为 true 时抛出
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_FALSE_MESSAGE));
        }
    }

    /**
     * 校验表达式是否为 false。
     *
     * @param expression 待校验表达式
     * @throws IllegalArgumentException 当 {@code expression} 为 true 时抛出
     */
    public static void isFalse(boolean expression) {
        isFalse(expression, DEFAULT_FALSE_MESSAGE);
    }

    /**
     * 校验对象不为 null。
     *
     * @param object 待校验对象
     * @param message 断言失败时的异常信息
     * @param <T> 对象类型
     * @return 校验通过后的对象本身
     * @throws IllegalArgumentException 当 {@code object} 为 null 时抛出
     */
    public static <T> T notNull(T object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NOT_NULL_MESSAGE));
        }
        return object;
    }

    /**
     * 校验对象不为 null。
     *
     * @param object 待校验对象
     * @param <T> 对象类型
     * @return 校验通过后的对象本身
     * @throws IllegalArgumentException 当 {@code object} 为 null 时抛出
     */
    public static <T> T notNull(T object) {
        return notNull(object, DEFAULT_NOT_NULL_MESSAGE);
    }

    /**
     * 校验对象为 null。
     *
     * @param object 待校验对象
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当 {@code object} 不为 null 时抛出
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NULL_MESSAGE));
        }
    }

    /**
     * 校验对象为 null。
     *
     * @param object 待校验对象
     * @throws IllegalArgumentException 当 {@code object} 不为 null 时抛出
     */
    public static void isNull(Object object) {
        isNull(object, DEFAULT_NULL_MESSAGE);
    }

    /**
     * 校验字符串长度大于 0。
     *
     * @param text 待校验字符串
     * @param message 断言失败时的异常信息
     * @return 校验通过后的字符串
     * @throws IllegalArgumentException 当 {@code text} 为 null 或空串时抛出
     */
    public static String hasLength(String text, String message) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_HAS_LENGTH_MESSAGE));
        }
        return text;
    }

    /**
     * 校验字符串长度大于 0。
     *
     * @param text 待校验字符串
     * @return 校验通过后的字符串
     * @throws IllegalArgumentException 当 {@code text} 为 null 或空串时抛出
     */
    public static String hasLength(String text) {
        return hasLength(text, DEFAULT_HAS_LENGTH_MESSAGE);
    }

    /**
     * 校验字符串包含至少一个非空白字符。
     *
     * @param text 待校验字符串
     * @param message 断言失败时的异常信息
     * @return 校验通过后的字符串
     * @throws IllegalArgumentException 当 {@code text} 为 null、空串或仅空白字符时抛出
     */
    public static String hasText(String text, String message) {
        if (!containsText(text)) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_HAS_TEXT_MESSAGE));
        }
        return text;
    }

    /**
     * 校验字符串包含至少一个非空白字符。
     *
     * @param text 待校验字符串
     * @return 校验通过后的字符串
     * @throws IllegalArgumentException 当 {@code text} 为 null、空串或仅空白字符时抛出
     */
    public static String hasText(String text) {
        return hasText(text, DEFAULT_HAS_TEXT_MESSAGE);
    }

    /**
     * 校验数组非空。
     *
     * @param array 待校验数组
     * @param message 断言失败时的异常信息
     * @param <T> 元素类型
     * @return 校验通过后的数组
     * @throws IllegalArgumentException 当 {@code array} 为 null 或空数组时抛出
     */
    public static <T> T[] notEmpty(T[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NOT_EMPTY_ARRAY_MESSAGE));
        }
        return array;
    }

    /**
     * 校验数组非空。
     *
     * @param array 待校验数组
     * @param <T> 元素类型
     * @return 校验通过后的数组
     * @throws IllegalArgumentException 当 {@code array} 为 null 或空数组时抛出
     */
    public static <T> T[] notEmpty(T[] array) {
        return notEmpty(array, DEFAULT_NOT_EMPTY_ARRAY_MESSAGE);
    }

    /**
     * 校验集合非空。
     *
     * @param collection 待校验集合
     * @param message 断言失败时的异常信息
     * @param <C> 集合类型
     * @return 校验通过后的集合
     * @throws IllegalArgumentException 当 {@code collection} 为 null 或空集合时抛出
     */
    public static <C extends Collection<?>> C notEmpty(C collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NOT_EMPTY_COLLECTION_MESSAGE));
        }
        return collection;
    }

    /**
     * 校验集合非空。
     *
     * @param collection 待校验集合
     * @param <C> 集合类型
     * @return 校验通过后的集合
     * @throws IllegalArgumentException 当 {@code collection} 为 null 或空集合时抛出
     */
    public static <C extends Collection<?>> C notEmpty(C collection) {
        return notEmpty(collection, DEFAULT_NOT_EMPTY_COLLECTION_MESSAGE);
    }

    /**
     * 校验 Map 非空。
     *
     * @param map 待校验 Map
     * @param message 断言失败时的异常信息
     * @param <M> Map 类型
     * @return 校验通过后的 Map
     * @throws IllegalArgumentException 当 {@code map} 为 null 或空 Map 时抛出
     */
    public static <M extends Map<?, ?>> M notEmpty(M map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NOT_EMPTY_MAP_MESSAGE));
        }
        return map;
    }

    /**
     * 校验 Map 非空。
     *
     * @param map 待校验 Map
     * @param <M> Map 类型
     * @return 校验通过后的 Map
     * @throws IllegalArgumentException 当 {@code map} 为 null 或空 Map 时抛出
     */
    public static <M extends Map<?, ?>> M notEmpty(M map) {
        return notEmpty(map, DEFAULT_NOT_EMPTY_MAP_MESSAGE);
    }

    /**
     * 校验数组中不包含 null 元素。
     *
     * @param array 待校验数组
     * @param message 断言失败时的异常信息
     * @param <T> 元素类型
     * @return 校验通过后的数组
     * @throws IllegalArgumentException 当 {@code array} 为 null 或包含 null 元素时抛出
     */
    public static <T> T[] noNullElements(T[] array, String message) {
        notNull(array, resolveMessage(message, DEFAULT_NO_NULL_ELEMENTS_ARRAY_MESSAGE));
        for (T element : array) {
            if (element == null) {
                throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NO_NULL_ELEMENTS_ARRAY_MESSAGE));
            }
        }
        return array;
    }

    /**
     * 校验数组中不包含 null 元素。
     *
     * @param array 待校验数组
     * @param <T> 元素类型
     * @return 校验通过后的数组
     * @throws IllegalArgumentException 当 {@code array} 为 null 或包含 null 元素时抛出
     */
    public static <T> T[] noNullElements(T[] array) {
        return noNullElements(array, DEFAULT_NO_NULL_ELEMENTS_ARRAY_MESSAGE);
    }

    /**
     * 校验集合中不包含 null 元素。
     *
     * @param collection 待校验集合
     * @param message 断言失败时的异常信息
     * @param <C> 集合类型
     * @return 校验通过后的集合
     * @throws IllegalArgumentException 当 {@code collection} 为 null 或包含 null 元素时抛出
     */
    public static <C extends Collection<?>> C noNullElements(C collection, String message) {
        notNull(collection, resolveMessage(message, DEFAULT_NO_NULL_ELEMENTS_COLLECTION_MESSAGE));
        for (Object element : collection) {
            if (element == null) {
                throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NO_NULL_ELEMENTS_COLLECTION_MESSAGE));
            }
        }
        return collection;
    }

    /**
     * 校验集合中不包含 null 元素。
     *
     * @param collection 待校验集合
     * @param <C> 集合类型
     * @return 校验通过后的集合
     * @throws IllegalArgumentException 当 {@code collection} 为 null 或包含 null 元素时抛出
     */
    public static <C extends Collection<?>> C noNullElements(C collection) {
        return noNullElements(collection, DEFAULT_NO_NULL_ELEMENTS_COLLECTION_MESSAGE);
    }

    /**
     * 校验对象是否为指定类型的实例。
     *
     * @param type 目标类型
     * @param object 待校验对象
     * @param message 断言失败时的异常信息
     * @param <T> 目标类型参数
     * @return 转换为目标类型后的对象
     * @throws IllegalArgumentException 当 {@code type} 为 null，或 {@code object} 不是 {@code type} 的实例时抛出
     */
    public static <T> T isInstanceOf(Class<T> type, Object object, String message) {
        notNull(type, "The target type to check against must not be null.");
        if (!type.isInstance(object)) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_INSTANCE_OF_MESSAGE));
        }
        return type.cast(object);
    }

    /**
     * 校验对象是否为指定类型的实例。
     *
     * @param type 目标类型
     * @param object 待校验对象
     * @param <T> 目标类型参数
     * @return 转换为目标类型后的对象
     * @throws IllegalArgumentException 当 {@code type} 为 null，或 {@code object} 不是 {@code type} 的实例时抛出
     */
    public static <T> T isInstanceOf(Class<T> type, Object object) {
        return isInstanceOf(type, object, DEFAULT_INSTANCE_OF_MESSAGE);
    }

    /**
     * 校验 subType 是否可赋值给 superType。
     *
     * @param superType 目标父类型
     * @param subType 待校验子类型
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当 {@code superType} 为 null，或 {@code subType} 为 null，
     * 或类型不可赋值时抛出
     */
    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        notNull(superType, "The super type to check against must not be null.");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_ASSIGNABLE_MESSAGE));
        }
    }

    /**
     * 校验 subType 是否可赋值给 superType。
     *
     * @param superType 目标父类型
     * @param subType 待校验子类型
     * @throws IllegalArgumentException 当 {@code superType} 为 null，或 {@code subType} 为 null，
     * 或类型不可赋值时抛出
     */
    public static void isAssignable(Class<?> superType, Class<?> subType) {
        isAssignable(superType, subType, DEFAULT_ASSIGNABLE_MESSAGE);
    }

    /**
     * 校验两个对象相等。
     *
     * @param expected 期望值
     * @param actual 实际值
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当两个对象不相等时抛出
     */
    public static void equals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_EQUALS_MESSAGE));
        }
    }

    /**
     * 校验两个对象相等。
     *
     * @param expected 期望值
     * @param actual 实际值
     * @throws IllegalArgumentException 当两个对象不相等时抛出
     */
    public static void equals(Object expected, Object actual) {
        equals(expected, actual, DEFAULT_EQUALS_MESSAGE);
    }

    /**
     * 校验两个对象不相等。
     *
     * @param unexpected 非期望值
     * @param actual 实际值
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当两个对象相等时抛出
     */
    public static void notEquals(Object unexpected, Object actual, String message) {
        if (Objects.equals(unexpected, actual)) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_NOT_EQUALS_MESSAGE));
        }
    }

    /**
     * 校验两个对象不相等。
     *
     * @param unexpected 非期望值
     * @param actual 实际值
     * @throws IllegalArgumentException 当两个对象相等时抛出
     */
    public static void notEquals(Object unexpected, Object actual) {
        notEquals(unexpected, actual, DEFAULT_NOT_EQUALS_MESSAGE);
    }

    /**
     * 校验字符串 text 不包含子串 substring。
     *
     * @param text 待校验字符串
     * @param substring 禁止包含的子串
     * @param message 断言失败时的异常信息
     * @throws IllegalArgumentException 当两个字符串都包含文本且 {@code text} 包含 {@code substring} 时抛出
     */
    public static void doesNotContain(String text, String substring, String message) {
        if (containsText(text) && containsText(substring) && text.contains(substring)) {
            throw new IllegalArgumentException(resolveMessage(message, DEFAULT_DOES_NOT_CONTAIN_MESSAGE));
        }
    }

    /**
     * 校验字符串 text 不包含子串 substring。
     *
     * @param text 待校验字符串
     * @param substring 禁止包含的子串
     * @throws IllegalArgumentException 当两个字符串都包含文本且 {@code text} 包含 {@code substring} 时抛出
     */
    public static void doesNotContain(String text, String substring) {
        doesNotContain(text, substring, DEFAULT_DOES_NOT_CONTAIN_MESSAGE);
    }

    private static String resolveMessage(String message, String fallback) {
        if (message == null || message.trim().isEmpty()) {
            return fallback;
        }
        return message;
    }

    private static boolean containsText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}
