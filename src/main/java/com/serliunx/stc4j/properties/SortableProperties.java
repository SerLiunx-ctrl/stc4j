package com.serliunx.stc4j.properties;

import com.serliunx.stc4j.util.Assert;
import com.serliunx.stc4j.util.IteratorToEnumerationAdapter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 配置管理: 按照顺序排列、线程不安全
 * <p>
 * 默认情况下使用UTF-8编码写入、读取配置文件及配置项的注释
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2025/8/26
 */
public class SortableProperties extends Properties implements ValueBasedProperties {

    /**
     * 默认的编码格式
     */
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * 配置项实际的存储位置
     */
    private final LinkedHashMap<Object, Object> valueMap = new LinkedHashMap<>(64);
    /**
     * 注释行
     */
    private final Map<String, List<String>> commentLine = new LinkedHashMap<>(64);

    @Override
    public int size() {
        return valueMap.size();
    }

    @Override
    public Object merge(Object key, Object value,
                                     BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return this.valueMap.merge(key, value, remappingFunction);
    }

    @Override
    public Object compute(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return this.valueMap.compute(key, remappingFunction);
    }

    @Override
    public Object computeIfPresent(Object key,
                                                BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return this.valueMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {
        return this.valueMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Object replace(Object key, Object value) {
        return this.valueMap.replace(key, value);
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object newValue) {
        return this.valueMap.replace(key, oldValue, newValue);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return this.valueMap.remove(key, value);
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        return this.valueMap.putIfAbsent(key, value);
    }

    @Override
    public void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {
        this.valueMap.replaceAll(function);
    }

    @Override
    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        this.valueMap.forEach(action);
    }

    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return this.valueMap.getOrDefault(key, defaultValue);
    }

    @Override
    public int hashCode() {
        return this.valueMap.hashCode();
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object o) {
        return this.valueMap.equals(o);
    }

    @Override
    public Collection<Object> values() {
        return this.valueMap.values();
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.valueMap.entrySet();
    }

    @Override
    public Set<Object> keySet() {
        return this.valueMap.keySet();
    }

    @Override
    public String toString() {
        return this.valueMap.toString();
    }

    @Override
    @SuppressWarnings("all")
    public Object clone() {
        return this.valueMap.clone();
    }

    @Override
    public void clear() {
        this.valueMap.clear();
    }

    @Override
    public void putAll(Map<?, ?> t) {
        this.valueMap.putAll(t);
    }

    @Override
    public Object remove(Object key) {
        return this.valueMap.remove(key);
    }

    @Override
    public Object put(Object key, Object value) {
        return this.valueMap.put(key, value);
    }

    @Override
    protected void rehash() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object key) {
        return this.valueMap.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.valueMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.valueMap.containsValue(value);
    }

    @Override
    public boolean contains(Object value) {
        return this.valueMap.containsValue(value);
    }

    @Override
    public Enumeration<Object> elements() {
        return new IteratorToEnumerationAdapter<>(valueMap.values().iterator());
    }

    @Override
    public Enumeration<Object> keys() {
        return new IteratorToEnumerationAdapter<>(valueMap.keySet().iterator());
    }

    @Override
    public boolean isEmpty() {
        return this.valueMap.isEmpty();
    }

    // Properties

    @Override
    public Object setProperty(String key, String value) {
        return valueMap.put(key, value);
    }

    @Override
    public void load(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        List<String> commentLines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#") || line.isEmpty()) {
                commentLines.add(line);
                continue;
            }

            String[] kv = line.split("=");
            if (kv.length < 1)
                continue;

            String key = kv[0];
            StringBuilder valBuilder = new StringBuilder();
            for (int i = 1; i < kv.length; i++) {
                valBuilder.append(kv[i]);
                if (i != kv.length - 1)
                    valBuilder.append("=");
            }
            String value = valBuilder.toString();
            put(key, value);

            if (!commentLines.isEmpty()) {
                System.out.println(key + "注释:" + Arrays.toString(commentLines.toArray()));
                setCommentLines(key, commentLines);
                commentLines.clear();
            }
        }
    }

    @Override
    public void load(InputStream inStream) throws IOException {
        this.load(new InputStreamReader(inStream, charset));
    }

    @Override
    @SuppressWarnings("all")
    public void save(OutputStream out, String comments) {
        try {
            this.store(out, comments);
        } catch (IOException ignored) {}
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        BufferedWriter bw = new BufferedWriter(writer);
        String firstLine = comments == null || comments.isEmpty() ? ("#" + new Date()) : comments;
        bw.write(firstLine);
        bw.newLine();
        synchronized (this) {
            for (Enumeration<?> e = keys(); e.hasMoreElements(); ) {
                String key = e.nextElement().toString();
                String val = get(key).toString();
                List<String> commentLines = getCommentLines(key);
                if (commentLines != null &&
                        !commentLines.isEmpty()) {
                    for (int i = 0; i < commentLines.size(); i++) {
                        try {
                            bw.write(commentLines.get(i));
                            if (i != commentLines.size() - 1) {
                                bw.newLine();
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    bw.newLine();
                }
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        this.store(new OutputStreamWriter(out), comments);
    }

    @Override
    public String getProperty(String key) {
        Object val = valueMap.get(key);
        if (val == null)
            return null;
        return val.toString();
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        Object val = valueMap.get(key);
        if (val == null)
            return defaultValue;
        return val.toString();
    }

    @Override
    public Enumeration<?> propertyNames() {
        return new IteratorToEnumerationAdapter<>(valueMap.keySet().iterator());
    }

    @Override
    public Set<String> stringPropertyNames() {
        return this.valueMap.keySet()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public void list(PrintStream out) {
        list(new PrintWriter(out));
    }

    @Override
    public void list(PrintWriter out) {
        out.println("-- listing properties --");
        valueMap.keySet()
                .iterator()
                .forEachRemaining(k -> {
                    String key, value;
                    if (k == null || (key = k.toString()).isEmpty())
                        return;

                    Object objVal = get(key);
                    if (objVal == null || (value = objVal.toString()).isEmpty())
                        value = "";
                    if (value.length() > 40)
                        value = value.substring(0, 37) + "...";
                    out.println(key + "=" + value);
                    out.flush();
                });
    }

    // === 增强部分

    /**
     * 在指定的配置键上方增加注释, 可添加多行
     *
     * @param key 配置键
     * @param comments 注释行数组
     * @throws IllegalArgumentException 配置键不存在时抛出.
     */
    public void setCommentLines(Object key, String...comments) {
        setCommentLines(key, Arrays.asList(comments));
    }

    /**
     * 在指定的配置键上方增加注释, 可添加多行
     *
     * @param key 配置键
     * @param comments 注释行列表
     * @throws IllegalArgumentException 配置键不存在时抛出.
     */
    public void setCommentLines(Object key, List<String> comments) {
        Assert.notNull(key, "key must not be null");
        if (!containsKey(key)) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        commentLine.put(key.toString(), new ArrayList<>(comments));
    }

    /**
     * 获取指定配置键的注释行
     *
     * @param key 配置键
     * @return 注释行
     */
    public List<String> getCommentLines(Object key) {
        Assert.notNull(key, "key must not be null");
        if (!containsKey(key)) {
            throw new IllegalArgumentException(key + " does not exist");
        }
        return commentLine.get(key.toString());
    }

    /**
     * 设置编码
     *
     * @param charset 编码
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * 获取编码
     *
     * @return 编码
     */
    public Charset getCharset() {
        return charset;
    }

    // ValueBasedProperties impl

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
}