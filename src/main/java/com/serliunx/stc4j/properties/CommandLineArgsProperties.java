package com.serliunx.stc4j.properties;

import java.util.Arrays;

/**
 * 配置类: 命令行参数解析
 * <p>
 * 常用于解析启动参数中的-Dxxx.xx=aaa之类的参数
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.3
 * @since 2026/3/6
 */
public class CommandLineArgsProperties extends MappedValueBasedProperties {

    private final String[] args;
    private final String delimiter;
    private final String propertyTag;

    public static final String DEFAULT_DELIMITER = "=";
    public static final String DEFAULT_PROPERTY_TAG = "-D";

    public CommandLineArgsProperties(String[] args, String delimiter, String propertyTag) {
        this.delimiter = delimiter;
        this.propertyTag = propertyTag;
        this.args = args;

        // 解析配置信息
        process();
    }

    public CommandLineArgsProperties(String[] args) {
        this(args, DEFAULT_DELIMITER, DEFAULT_PROPERTY_TAG);
    }

    private void process() {
        if (args.length == 0) {
            return;
        }

        Arrays.stream(args)
                .filter(s -> s.startsWith(propertyTag) && s.contains(delimiter))
                .forEach(s -> {
                    final int di = s.indexOf(delimiter, propertyTag.length());
                    final String key = s.substring(propertyTag.length(), di);
                    final String value = s.substring(di + delimiter.length());
                    setProperty(key, value);
                });
    }
}
