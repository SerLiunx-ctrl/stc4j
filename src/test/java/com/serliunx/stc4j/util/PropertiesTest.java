package com.serliunx.stc4j.util;

import com.serliunx.stc4j.properties.SortableProperties;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * 配置文件测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.2
 * @since 2026/3/6
 */
public class PropertiesTest {

    @Test
    public void testProperties() throws Exception {
        Properties properties = new SortableProperties();
        properties.put("key1", "value1");
        properties.put("key2", "测试");
        Path f = Paths.get("properties.txt");
        properties.store(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(f))), "你好");

        Properties properties2 = new SortableProperties();
        // 读取
        properties2.load(new BufferedReader(new InputStreamReader(Files.newInputStream(f))));
        properties2.forEach((key, value) -> System.out.println(key + ": " + value));

        // 删除文件
        Files.deleteIfExists(f);
    }

    @Test
    public void testPropertiesReadWithComments() throws Exception {
        SortableProperties properties = new SortableProperties();
        properties.load(Files.newInputStream(Paths.get("properties.txt")));

        List<String> commentLines = properties.getCommentLines("key2");
        commentLines.forEach(System.out::println);
    }

    @Test
    public void testPropertiesWriteWithComments() throws Exception {
        SortableProperties properties = new SortableProperties();
        properties.put("key1", "value1");
        properties.put("key2", "v2");
        properties.setCommentLines("key1", "# 第一行注释", "# 第二行注释");
        properties.setCommentLines("key2", "", "# 第三行注释");

        properties.save(Files.newOutputStream(Paths.get("properties.txt")), "");
    }
}
