package com.serliunx.stc4j.properties;

import com.serliunx.stc4j.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Map的配置管理, 仅定义基础实现.
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.3
 * @since 2026/3/6
 * @see CommandLineArgsProperties
 */
public abstract class MappedValueBasedProperties implements ValueBasedProperties {

    /**
     * 配置值
     */
    protected final Map<String, String> values = new LinkedHashMap<>(64);

    /**
     * 获取配置信息
     *
     * @param key 配置键
     * @return 配置信息
     */
    protected final String getProperty(String key) {
        return values.get(key);
    }

    /**
     * 设置配置值
     *
     * @param key 配置键
     * @param value 配置值
     */
    protected final void setProperty(String key, String value) {
        values.put(key, value);
    }

    @Override
    public String getString(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return property;
    }

    @Override
    public String getString(String key, String defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return property;
    }

    @Override
    public int getInteger(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return Integer.parseInt(property);
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Integer.parseInt(property);
    }

    @Override
    public long getLong(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return Long.parseLong(property);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Long.parseLong(property);
    }

    @Override
    public double getDouble(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return Double.parseDouble(property);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Double.parseDouble(property);
    }

    @Override
    public float getFloat(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return Float.parseFloat(property);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Float.parseFloat(property);
    }

    @Override
    public boolean getBoolean(String key) {
        String property = getProperty(key);
        if (property == null) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return Boolean.parseBoolean(property);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        String property = getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(property);
    }

    @Override
    public MappedValueBasedProperties merge(ValueBasedProperties other) {
        List<Pair<String, String>> ps = other.allProperties();
        if (ps != null && !ps.isEmpty()) {
            ps.forEach(p -> setProperty(p.left(), p.right()));
        }
        return this;
    }

    @Override
    public List<Pair<String, String>> allProperties() {
        return Pair.extract(values);
    }
}
