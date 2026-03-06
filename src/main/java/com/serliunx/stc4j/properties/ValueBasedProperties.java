package com.serliunx.stc4j.properties;

/**
 * 基于值类型读取配置项的接口。
 * <p>
 * 该接口在 {@link java.util.Properties} 的字符串读取能力之上，提供了按常见基础类型
 * 直接转换配置值的方法，调用方无需重复编写类型转换逻辑。
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.2
 * @since 2026/3/6
 */
public interface ValueBasedProperties {

    /**
     * 获取指定配置项并转换为 {@code int}。
     *
     * @param key 配置键
     * @return 配置键对应的整数值
     * @throws IllegalArgumentException 当配置项不存在时可能抛出
     * @throws NumberFormatException 当配置值无法转换为 {@code int} 时抛出
     */
    int getInteger(String key);

    /**
     * 获取指定配置项并转换为 {@code int}，当配置项不存在时返回默认值。
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置键对应的整数值；若配置项不存在，则返回 {@code defaultValue}
     * @throws NumberFormatException 当配置值存在但无法转换为 {@code int} 时抛出
     */
    int getInteger(String key, int defaultValue);

    /**
     * 获取指定配置项并转换为 {@code long}。
     *
     * @param key 配置键
     * @return 配置键对应的长整数值
     * @throws IllegalArgumentException 当配置项不存在时可能抛出
     * @throws NumberFormatException 当配置值无法转换为 {@code long} 时抛出
     */
    long getLong(String key);

    /**
     * 获取指定配置项并转换为 {@code long}，当配置项不存在时返回默认值。
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置键对应的长整数值；若配置项不存在，则返回 {@code defaultValue}
     * @throws NumberFormatException 当配置值存在但无法转换为 {@code long} 时抛出
     */
    long getLong(String key, long defaultValue);

    /**
     * 获取指定配置项并转换为 {@code double}。
     *
     * @param key 配置键
     * @return 配置键对应的双精度浮点值
     * @throws IllegalArgumentException 当配置项不存在时可能抛出
     * @throws NumberFormatException 当配置值无法转换为 {@code double} 时抛出
     */
    double getDouble(String key);

    /**
     * 获取指定配置项并转换为 {@code double}，当配置项不存在时返回默认值。
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置键对应的双精度浮点值；若配置项不存在，则返回 {@code defaultValue}
     * @throws NumberFormatException 当配置值存在但无法转换为 {@code double} 时抛出
     */
    double getDouble(String key, double defaultValue);

    /**
     * 获取指定配置项并转换为 {@code float}。
     *
     * @param key 配置键
     * @return 配置键对应的单精度浮点值
     * @throws IllegalArgumentException 当配置项不存在时可能抛出
     * @throws NumberFormatException 当配置值无法转换为 {@code float} 时抛出
     */
    float getFloat(String key);

    /**
     * 获取指定配置项并转换为 {@code float}，当配置项不存在时返回默认值。
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置键对应的单精度浮点值；若配置项不存在，则返回 {@code defaultValue}
     * @throws NumberFormatException 当配置值存在但无法转换为 {@code float} 时抛出
     */
    float getFloat(String key, float defaultValue);

    /**
     * 获取指定配置项并转换为 {@code boolean}。
     *
     * @param key 配置键
     * @return 配置键对应的布尔值
     * @throws IllegalArgumentException 当配置项不存在时可能抛出
     */
    boolean getBoolean(String key);

    /**
     * 获取指定配置项并转换为 {@code boolean}，当配置项不存在时返回默认值。
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置键对应的布尔值；若配置项不存在，则返回 {@code defaultValue}
     */
    boolean getBoolean(String key, boolean defaultValue);
}
