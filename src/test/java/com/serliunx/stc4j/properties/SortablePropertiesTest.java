package com.serliunx.stc4j.properties;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link SortableProperties} 测试逻辑全覆盖
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class SortablePropertiesTest {

    @Test
    public void testLoadReaderPreservesValuesCommentsAndOrder() throws Exception {
        SortableProperties properties = new SortableProperties();
        String content = "# global comment\n"
                + "# key1 comment\n"
                + "key1=value1\n"
                + "\n"
                + "# key2 comment\n"
                + "key2=hello=world\n";

        properties.load(new StringReader(content));

        assertEquals("value1", properties.getString("key1"));
        assertEquals("hello=world", properties.getString("key2"));
        assertEquals(Arrays.asList("# global comment", "# key1 comment"),
                properties.getCommentLines("key1"));
        assertEquals(Arrays.asList("", "# key2 comment"),
                properties.getCommentLines("key2"));
        assertEquals(Arrays.asList("key1", "key2"), new ArrayList<>(properties.stringPropertyNames()));
    }

    @Test
    public void testStoreWriterWritesHeaderCommentsAndPropertiesInOrder() throws Exception {
        SortableProperties properties = new SortableProperties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "hello=world");
        properties.setCommentLines("key1", "# first comment", "# second comment");
        properties.setCommentLines("key2", Collections.singletonList("# key2 comment"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        properties.store(out, "# file header");

        String serialized = new String(out.toByteArray(), StandardCharsets.UTF_8);

        assertEquals("# file header\n"
                        + "# first comment\n"
                        + "# second comment\n"
                        + "key1=value1\n"
                        + "# key2 comment\n"
                        + "key2=hello=world\n",
                serialized.replace("\r\n", "\n"));
    }

    @Test
    public void testLoadAndStoreRoundTripPreservesCommentsAndUtf8Values() throws Exception {
        SortableProperties written = new SortableProperties();
        written.setProperty("title", "你好");
        written.setProperty("url", "jdbc:mysql://localhost:3306/demo?useUnicode=true");
        written.setCommentLines("title", "# title comment");
        written.setCommentLines("url", "", "# url comment");

        Path tempFile = Files.createTempFile("sortable-properties-", ".txt");
        try {
            written.store(Files.newOutputStream(tempFile), "# header");

            SortableProperties loaded = new SortableProperties();
            loaded.load(Files.newInputStream(tempFile));

            assertEquals("你好", loaded.getString("title"));
            assertEquals("jdbc:mysql://localhost:3306/demo?useUnicode=true", loaded.getString("url"));
            assertEquals(Arrays.asList("# header", "# title comment"), loaded.getCommentLines("title"));
            assertEquals(Arrays.asList("", "# url comment"), loaded.getCommentLines("url"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testSaveUsesCharsetForOutputStreamRoundTrip() throws Exception {
        SortableProperties written = new SortableProperties();
        written.setCharset(StandardCharsets.UTF_8);
        written.setProperty("message", "测试");
        written.setCommentLines("message", "# 注释");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        written.save(out, "# 头部");

        SortableProperties loaded = new SortableProperties();
        loaded.setCharset(StandardCharsets.UTF_8);
        loaded.load(new ByteArrayInputStream(out.toByteArray()));

        String serialized = new String(out.toByteArray(), StandardCharsets.UTF_8).replace("\r\n", "\n");

        assertEquals("测试", loaded.getString("message"));
        assertEquals(Arrays.asList("# 头部", "# 注释"), loaded.getCommentLines("message"));
        assertTrue(serialized.startsWith("# 头部\n"));
    }
}
