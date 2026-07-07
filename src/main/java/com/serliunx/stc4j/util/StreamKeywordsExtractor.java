package com.serliunx.stc4j.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 流式关键词提取器，用于从大模型流式返回的文本中提取特殊标记。
 * <p>
 * 支持提取以下模式：
 * <ul>
 * <li>HTML 标签：{@code <tag attr="val">body</tag>} 或自闭合形式 {@code <tag attr="val"/>}</li>
 * <li>变量：{@code %variable_name%}</li>
 * </ul>
 * 适用于 SSE 流式响应场景，能够正确处理跨 chunk 的部分匹配。
 *
 * <pre>{@code
 * StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
 * List<Keyword> keywords = extractor.feed("<img src=\"url\">desc</img>");
 * for (Keyword kw : keywords) {
 *     System.out.println(kw.type() + " -> " + kw.value());
 * }
 * }</pre>
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/5/29
 */
public class StreamKeywordsExtractor {

    private enum State {
        NORMAL,
        TAG_OPEN,
        TAG_BODY,
        TAG_CLOSE,
        VARIABLE
    }

    private State currentState = State.NORMAL;

    private final StringBuilder textBuffer = new StringBuilder();
    private final StringBuilder tagBuffer = new StringBuilder();
    private final StringBuilder bodyBuffer = new StringBuilder();
    private final StringBuilder variableBuffer = new StringBuilder();

    private String currentTagName;
    private String currentTagRaw;
    private boolean selfClosing;
    private Map<String, String> currentAttributes;

    public List<Keyword> feed(String chunk) {
        if (chunk == null || chunk.isEmpty()) {
            return Collections.emptyList();
        }

        List<Keyword> result = new ArrayList<Keyword>();

        for (int i = 0; i < chunk.length(); i++) {
            char c = chunk.charAt(i);

            switch (currentState) {
                case NORMAL:
                    processNormal(c, result);
                    break;
                case TAG_OPEN:
                    processTagOpen(c, result);
                    break;
                case TAG_BODY:
                    processTagBody(c, result);
                    break;
                case TAG_CLOSE:
                    processTagClose(c, result);
                    break;
                case VARIABLE:
                    processVariable(c, result);
                    break;
            }
        }

        return result;
    }

    public List<Keyword> finish() {
        List<Keyword> result = new ArrayList<Keyword>();

        switch (currentState) {
            case VARIABLE:
                textBuffer.append('%').append(variableBuffer);
                break;
            case TAG_OPEN:
                textBuffer.append(tagBuffer);
                break;
            case TAG_BODY:
                if (currentTagRaw != null) {
                    textBuffer.append(currentTagRaw);
                }
                textBuffer.append(bodyBuffer);
                break;
            case TAG_CLOSE:
                if (currentTagRaw != null) {
                    textBuffer.append(currentTagRaw);
                }
                textBuffer.append(bodyBuffer);
                textBuffer.append(tagBuffer);
                break;
            default:
                break;
        }

        flushText(result);
        currentState = State.NORMAL;
        return result;
    }

    public void reset() {
        currentState = State.NORMAL;
        textBuffer.setLength(0);
        tagBuffer.setLength(0);
        bodyBuffer.setLength(0);
        variableBuffer.setLength(0);
        currentTagName = null;
        currentTagRaw = null;
        selfClosing = false;
        currentAttributes = null;
    }

    private void processNormal(char c, List<Keyword> result) {
        if (c == '<') {
            currentState = State.TAG_OPEN;
            tagBuffer.setLength(0);
            tagBuffer.append(c);
            selfClosing = false;
            currentAttributes = new LinkedHashMap<String, String>();
        } else if (c == '%') {
            currentState = State.VARIABLE;
            variableBuffer.setLength(0);
        } else {
            textBuffer.append(c);
        }
    }

    private void processTagOpen(char c, List<Keyword> result) {
        tagBuffer.append(c);
        int len = tagBuffer.length();

        if (len == 2 && !Character.isLetter(c)) {
            textBuffer.append(tagBuffer);
            currentState = State.NORMAL;
            return;
        }

        if (c == '>') {
            currentTagRaw = tagBuffer.toString();
            flushText(result);
            parseOpeningTag(currentTagRaw);
            if (selfClosing) {
                result.add(new Keyword(currentTagName, null, currentAttributes));
                currentState = State.NORMAL;
            } else {
                bodyBuffer.setLength(0);
                currentState = State.TAG_BODY;
            }
        }
    }

    private void processTagBody(char c, List<Keyword> result) {
        if (c == '<') {
            currentState = State.TAG_CLOSE;
            tagBuffer.setLength(0);
            tagBuffer.append(c);
        } else {
            bodyBuffer.append(c);
        }
    }

    private void processTagClose(char c, List<Keyword> result) {
        tagBuffer.append(c);
        int len = tagBuffer.length();

        if (len == 2) {
            if (c != '/' && !Character.isLetter(c)) {
                bodyBuffer.append(tagBuffer);
                currentState = State.TAG_BODY;
                return;
            }
        }

        if (c == '>') {
            String closeTag = tagBuffer.toString();
            if (closeTag.startsWith("</")) {
                String endName = closeTag.substring(2, closeTag.length() - 1).trim().toLowerCase();
                if (endName.equals(currentTagName)) {
                    result.add(new Keyword(currentTagName, bodyBuffer.toString(), currentAttributes));
                    currentState = State.NORMAL;
                    return;
                }
            }
            bodyBuffer.append(tagBuffer);
            currentState = State.TAG_BODY;
        }
    }

    private void processVariable(char c, List<Keyword> result) {
        if (c == '%') {
            String varName = variableBuffer.toString();
            if (varName.isEmpty()) {
                textBuffer.append('%').append('%');
            } else {
                flushText(result);
                result.add(new Keyword("variable", varName, Collections.<String, String>emptyMap()));
            }
            currentState = State.NORMAL;
        } else if (Character.isWhitespace(c)) {
            textBuffer.append('%').append(variableBuffer).append(c);
            currentState = State.NORMAL;
        } else {
            variableBuffer.append(c);
        }
    }

    private void parseOpeningTag(String tagContent) {
        String inner = tagContent.substring(1, tagContent.length() - 1).trim();

        if (inner.endsWith("/")) {
            selfClosing = true;
            inner = inner.substring(0, inner.length() - 1).trim();
        }

        int spaceIndex = findFirstWhitespace(inner);
        if (spaceIndex == -1) {
            currentTagName = inner.toLowerCase();
        } else {
            currentTagName = inner.substring(0, spaceIndex).toLowerCase();
            parseAttributes(inner.substring(spaceIndex + 1).trim());
        }
    }

    private void parseAttributes(String attrString) {
        int i = 0;
        int length = attrString.length();
        while (i < length) {
            while (i < length && Character.isWhitespace(attrString.charAt(i))) {
                i++;
            }
            if (i >= length) {
                break;
            }

            int keyStart = i;
            while (i < length && attrString.charAt(i) != '=' && !Character.isWhitespace(attrString.charAt(i))) {
                i++;
            }
            String key = attrString.substring(keyStart, i).toLowerCase();

            while (i < length && Character.isWhitespace(attrString.charAt(i))) {
                i++;
            }

            if (i < length && attrString.charAt(i) == '=') {
                i++;
                while (i < length && Character.isWhitespace(attrString.charAt(i))) {
                    i++;
                }

                if (i < length) {
                    char quote = attrString.charAt(i);
                    if (quote == '"' || quote == '\'') {
                        i++;
                        int valueStart = i;
                        while (i < length && attrString.charAt(i) != quote) {
                            i++;
                        }
                        currentAttributes.put(key, attrString.substring(valueStart, i));
                        if (i < length) {
                            i++;
                        }
                    } else {
                        int valueStart = i;
                        while (i < length && !Character.isWhitespace(attrString.charAt(i))) {
                            i++;
                        }
                        currentAttributes.put(key, attrString.substring(valueStart, i));
                    }
                }
            } else {
                currentAttributes.put(key, "");
            }
        }
    }

    private static int findFirstWhitespace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private void flushText(List<Keyword> result) {
        if (textBuffer.length() > 0) {
            result.add(new Keyword("text", textBuffer.toString(), Collections.<String, String>emptyMap()));
            textBuffer.setLength(0);
        }
    }

    /**
     * 提取到的关键词。
     */
    public static final class Keyword {

        private final String type;
        private final String value;
        private final Map<String, String> attributes;

        public Keyword(String type, String value, Map<String, String> attributes) {
            this.type = type;
            this.value = value;
            this.attributes = attributes != null
                    ? Collections.unmodifiableMap(new LinkedHashMap<String, String>(attributes))
                    : Collections.<String, String>emptyMap();
        }

        public String type() {
            return type;
        }

        public String value() {
            return value;
        }

        public Map<String, String> attributes() {
            return attributes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Keyword)) {
                return false;
            }
            Keyword keyword = (Keyword) o;
            return Objects.equals(type, keyword.type)
                    && Objects.equals(value, keyword.value)
                    && Objects.equals(attributes, keyword.attributes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value, attributes);
        }

        @Override
        public String toString() {
            return "Keyword{"
                    + "type='" + type + '\''
                    + ", value='" + value + '\''
                    + ", attributes=" + attributes
                    + '}';
        }
    }
}
