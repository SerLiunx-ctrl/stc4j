package com.serliunx.stc4j.properties;

import com.serliunx.stc4j.util.Pair;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * {@link ValueBasedProperties} 测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/16
 */
public class ValueBasedPropertiesTest {

    @Test
    public void testCommandLineArgsPropertiesAllPropertiesPreservesInsertionOrder() {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[]{
                "-Dspring.name=jack",
                "-Dserver.port=1080",
                "-Dfeature.enabled=true"
        });

        List<Pair<String, String>> allProperties = properties.allProperties();

        assertEquals(3, allProperties.size());
        assertEquals(Pair.of("spring.name", "jack"), allProperties.get(0));
        assertEquals(Pair.of("server.port", "1080"), allProperties.get(1));
        assertEquals(Pair.of("feature.enabled", "true"), allProperties.get(2));
    }

    @Test
    public void testDefaultValueBasedPropertiesMergeOverwritesExistingValues() {
        DefaultValueBasedProperties properties = new DefaultValueBasedProperties();

        ValueBasedProperties merged = properties
                .merge(new CommandLineArgsProperties(new String[]{
                        "-Dspring.name=jack",
                        "-Dserver.port=1080"
                }))
                .merge(new CommandLineArgsProperties(new String[]{
                        "-Dserver.port=2080",
                        "-Dfeature.enabled=false"
                }));

        assertSame(properties, merged);
        assertEquals("jack", properties.getString("spring.name"));
        assertEquals(2080, properties.getInteger("server.port"));
        assertFalse(properties.getBoolean("feature.enabled"));
    }

    @Test
    public void testDefaultValueBasedPropertiesAllPropertiesReturnsMergedEntries() {
        DefaultValueBasedProperties properties = new DefaultValueBasedProperties();
        properties.merge(new CommandLineArgsProperties(new String[]{
                "-Dspring.name=jack",
                "-Dserver.port=1080"
        }));

        List<Pair<String, String>> allProperties = properties.allProperties();

        assertEquals(2, allProperties.size());
        assertEquals(Pair.of("spring.name", "jack"), allProperties.get(0));
        assertEquals(Pair.of("server.port", "1080"), allProperties.get(1));
    }

    @Test
    public void testSortablePropertiesMergeAndAllPropertiesConvertsNullValueToEmptyString() {
        SortableProperties properties = new SortableProperties();
        properties.put("self.key", null);

        SortableProperties merged = properties.merge(new CommandLineArgsProperties(new String[]{
                "-Dspring.name=jack",
                "-Dserver.port=1080"
        }));

        List<Pair<String, String>> allProperties = properties.allProperties();

        assertSame(properties, merged);
        assertEquals(3, allProperties.size());
        assertEquals(Pair.of("self.key", ""), allProperties.get(0));
        assertEquals(Pair.of("spring.name", "jack"), allProperties.get(1));
        assertEquals(Pair.of("server.port", "1080"), allProperties.get(2));
    }
}
