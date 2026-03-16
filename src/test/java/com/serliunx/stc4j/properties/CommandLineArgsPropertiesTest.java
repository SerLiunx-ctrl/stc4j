package com.serliunx.stc4j.properties;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * {@link ValueBasedProperties} 配置测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @since 2026/3/6
 */
public class CommandLineArgsPropertiesTest {

    @Test
    public void testCommandLineArgsPropertiesSupportsTypedAccessors() {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[]{
                "-Dspring.name=jack",
                "-Dserver.port=1080",
                "-Drequest.timeout=3000",
                "-Dmetrics.rate=0.75",
                "-Dcache.factor=0.5",
                "-Dfeature.enabled=true"
        });

        assertEquals("jack", properties.getString("spring.name"));
        assertEquals(1080, properties.getInteger("server.port"));
        assertEquals(3000L, properties.getLong("request.timeout"));
        assertEquals(0.75d, properties.getDouble("metrics.rate"), 0.00001d);
        assertEquals(0.5f, properties.getFloat("cache.factor"), 0.00001f);
        assertTrue(properties.getBoolean("feature.enabled"));
    }

    @Test
    public void testCommandLineArgsPropertiesReturnsDefaultsWhenKeyMissing() {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[]{
                "-Dspring.name=jack"
        });

        assertEquals("fallback", properties.getString("missing.string", "fallback"));
        assertEquals(8080, properties.getInteger("missing.int", 8080));
        assertEquals(3000L, properties.getLong("missing.long", 3000L));
        assertEquals(0.75d, properties.getDouble("missing.double", 0.75d), 0.00001d);
        assertEquals(0.5f, properties.getFloat("missing.float", 0.5f), 0.00001f);
        assertFalse(properties.getBoolean("missing.boolean", false));
        assertTrue(properties.getBoolean("missing.boolean.true", true));
    }

    @Test
    public void testCommandLineArgsPropertiesThrowsWhenRequiredKeyMissing() {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[0]);

        assertThrows(IllegalArgumentException.class, () -> properties.getString("missing"));
        assertThrows(IllegalArgumentException.class, () -> properties.getInteger("missing"));
        assertThrows(IllegalArgumentException.class, () -> properties.getLong("missing"));
        assertThrows(IllegalArgumentException.class, () -> properties.getDouble("missing"));
        assertThrows(IllegalArgumentException.class, () -> properties.getFloat("missing"));
        assertThrows(IllegalArgumentException.class, () -> properties.getBoolean("missing"));
    }

    @Test
    public void testCommandLineArgsPropertiesIgnoresUnsupportedArgsAndSupportsCustomSyntax() {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[]{
                "--server.port:9090",
                "--feature.enabled:false",
                "-Dignored=1",
                "--missing-delimiter"
        }, ":", "--");

        assertEquals(9090, properties.getInteger("server.port"));
        assertFalse(properties.getBoolean("feature.enabled"));
        assertEquals("fallback", properties.getString("ignored", "fallback"));
    }

    @Test
    public void testCommandLineArgsPropertiesKeepsDelimiterCharactersInsideValue() {
        ValueBasedProperties properties = new CommandLineArgsProperties(new String[]{
                "-Djdbc.url=jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=UTF-8",
                "-Dmessage=hello=world"
        });

        assertEquals("jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=UTF-8",
                properties.getString("jdbc.url"));
        assertEquals("hello=world", properties.getString("message"));
    }
}
