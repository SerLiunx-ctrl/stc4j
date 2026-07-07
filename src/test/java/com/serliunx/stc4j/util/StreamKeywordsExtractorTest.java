package com.serliunx.stc4j.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 流式关键词提取器测试
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.5
 * @since 2026/5/29
 */
public class StreamKeywordsExtractorTest {

    // ==================== 基本功能 ====================

    @Test
    public void testVariable() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("hello %world% end");

        assertEquals(2, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("hello ", keywords.get(0).value());
        assertEquals("variable", keywords.get(1).type());
        assertEquals("world", keywords.get(1).value());

        List<StreamKeywordsExtractor.Keyword> tail = extractor.finish();
        assertEquals(1, tail.size());
        assertEquals("text", tail.get(0).type());
        assertEquals(" end", tail.get(0).value());
    }

    @Test
    public void testSelfClosingTag() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img src=\"http://example.com/a.jpg\"/>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertTrue(keywords.get(0).attributes().containsKey("src"));
        assertEquals("http://example.com/a.jpg", keywords.get(0).attributes().get("src"));
    }

    @Test
    public void testTagWithBody() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img>http://example.com/b.jpg</img>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("http://example.com/b.jpg", keywords.get(0).value());
    }

    @Test
    public void testTagWithAttributesAndBody() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "<video src=\"http://example.com/v.mp4\" width=\"640\">sample video</video>");

        assertEquals(1, keywords.size());
        assertEquals("video", keywords.get(0).type());
        assertEquals("sample video", keywords.get(0).value());
        assertEquals("640", keywords.get(0).attributes().get("width"));
        assertEquals("http://example.com/v.mp4", keywords.get(0).attributes().get("src"));
    }

    @Test
    public void testMultipleKeywords() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "hi %name%, see <img src='x.png'/> and <video>clip</video> end");

        assertEquals(6, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("hi ", keywords.get(0).value());
        assertEquals("variable", keywords.get(1).type());
        assertEquals("name", keywords.get(1).value());
        assertEquals("text", keywords.get(2).type());
        assertEquals(", see ", keywords.get(2).value());
        assertEquals("img", keywords.get(3).type());
        assertEquals("text", keywords.get(4).type());
        assertEquals(" and ", keywords.get(4).value());
        assertEquals("video", keywords.get(5).type());
        assertEquals("clip", keywords.get(5).value());

        List<StreamKeywordsExtractor.Keyword> tail = extractor.finish();
        assertEquals(1, tail.size());
        assertEquals("text", tail.get(0).type());
        assertEquals(" end", tail.get(0).value());
    }

    // ==================== 流式跨 chunk ====================

    @Test
    public void testStreamingVariableChunks() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("pre %cur");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed("rent_time% post");
        assertEquals(2, k2.size());
        assertEquals("text", k2.get(0).type());
        assertEquals("pre ", k2.get(0).value());
        assertEquals("variable", k2.get(1).type());
        assertEquals("current_time", k2.get(1).value());

        List<StreamKeywordsExtractor.Keyword> k3 = extractor.finish();
        assertEquals(1, k3.size());
        assertEquals("text", k3.get(0).type());
        assertEquals(" post", k3.get(0).value());
    }

    @Test
    public void testStreamingTagChunks() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("<im");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed("g>hello</img>");
        assertEquals(1, k2.size());
        assertEquals("img", k2.get(0).type());
        assertEquals("hello", k2.get(0).value());
    }

    @Test
    public void testStreamingAngleBracketOnly() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("pre <");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed("img>body</img>");
        assertEquals(2, k2.size());
        assertEquals("text", k2.get(0).type());
        assertEquals("pre ", k2.get(0).value());
        assertEquals("img", k2.get(1).type());
        assertEquals("body", k2.get(1).value());
    }

    @Test
    public void testStreamingCloseTagChunks() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("<img>body</im");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed("g>");
        assertEquals(1, k2.size());
        assertEquals("img", k2.get(0).type());
        assertEquals("body", k2.get(0).value());
    }

    @Test
    public void testStreamingGtOnlyChunk() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("<img");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed(">body</img>");
        assertEquals(1, k2.size());
        assertEquals("img", k2.get(0).type());
        assertEquals("body", k2.get(0).value());
    }

    @Test
    public void testStreamingAttributeChunks() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("<img src=\"http://exa");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed("mple.com/a.jpg\"/>");
        assertEquals(1, k2.size());
        assertEquals("img", k2.get(0).type());
        assertEquals("http://example.com/a.jpg", k2.get(0).attributes().get("src"));
    }

    // ==================== finish / 未闭合处理 ====================

    @Test
    public void testFinish() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("hello world");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("hello world", keywords.get(0).value());
    }

    @Test
    public void testFinishUnclosedVariable() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("value is %unclosed");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("value is %unclosed", keywords.get(0).value());
    }

    @Test
    public void testFinishUnclosedTag() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<img>partial body");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("<img>partial body", keywords.get(0).value());
    }

    @Test
    public void testFinishUnclosedTagOpenOnly() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<video");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("<video", keywords.get(0).value());
    }

    @Test
    public void testFinishUnclosedTagInBody() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<img>hello</im");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("<img>hello</im", keywords.get(0).value());
    }

    @Test
    public void testFinishMismatchedClose() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<img>abc</video>");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("<img>abc</video>", keywords.get(0).value());
    }

    // ==================== 变量边界 ====================

    @Test
    public void testEmptyVariable() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("a %% b");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("a %% b", keywords.get(0).value());
    }

    @Test
    public void testWhitespaceInVariable() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("50% complete");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("50% complete", keywords.get(0).value());
    }

    @Test
    public void testVariableFollowedBySpaceAbort() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("abc%def ghi");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("abc%def ghi", keywords.get(0).value());
    }

    @Test
    public void testVariableWithUnderscore() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<%current_time%>");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("<%current_time%>", keywords.get(0).value());
    }

    // ==================== 属性解析边界 ====================

    @Test
    public void testUnquotedAttribute() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img src=a.jpg/>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("a.jpg", keywords.get(0).attributes().get("src"));
    }

    @Test
    public void testSingleQuotedAttribute() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img src='a.jpg'/>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("a.jpg", keywords.get(0).attributes().get("src"));
    }

    @Test
    public void testBooleanAttribute() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<video controls/>");

        assertEquals(1, keywords.size());
        assertEquals("video", keywords.get(0).type());
        assertTrue(keywords.get(0).attributes().containsKey("controls"));
        assertEquals("", keywords.get(0).attributes().get("controls"));
    }

    @Test
    public void testAttributeValueWithSpace() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "<img alt=\"hello world\" src=\"a.jpg\"/>");

        assertEquals(1, keywords.size());
        assertEquals("hello world", keywords.get(0).attributes().get("alt"));
        assertEquals("a.jpg", keywords.get(0).attributes().get("src"));
    }

    @Test
    public void testMultipleBooleanAttributes() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "<input disabled readonly type=\"text\"/>");

        assertEquals(1, keywords.size());
        assertEquals("input", keywords.get(0).type());
        assertEquals("", keywords.get(0).attributes().get("disabled"));
        assertEquals("", keywords.get(0).attributes().get("readonly"));
        assertEquals("text", keywords.get(0).attributes().get("type"));
    }

    @Test
    public void testUppercaseTagAndAttr() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "<IMG SRC=\"a.jpg\" ALT=\"pic\"/>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("a.jpg", keywords.get(0).attributes().get("src"));
        assertEquals("pic", keywords.get(0).attributes().get("alt"));
    }

    // ==================== 非法标签回退 ====================

    @Test
    public void testInvalidTagName() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("hello < 123");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.finish();
        assertEquals(1, k2.size());
        assertEquals("text", k2.get(0).type());
        assertEquals("hello < 123", k2.get(0).value());
    }

    @Test
    public void testAngleWithDigit() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("a <3 b");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("a <3 b", keywords.get(0).value());
    }

    @Test
    public void testAngleWithPunctuation() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("a <. b");
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.finish();

        assertEquals(1, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("a <. b", keywords.get(0).value());
    }

    @Test
    public void testNestedAngleBracketsInBody() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img>3 < 5 is true</img>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("3 < 5 is true", keywords.get(0).value());
    }

    // ==================== 不匹配闭合标签 ====================

    @Test
    public void testMismatchedCloseTagInBody() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img>abc</video></img>");

        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("abc</video>", keywords.get(0).value());
    }

    // ==================== reset / 多轮复用 ====================

    @Test
    public void testReset() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<img>incomplete");
        extractor.reset();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("fresh %start%");

        assertEquals(2, keywords.size());
        assertEquals("text", keywords.get(0).type());
        assertEquals("fresh ", keywords.get(0).value());
        assertEquals("variable", keywords.get(1).type());
        assertEquals("start", keywords.get(1).value());
    }

    @Test
    public void testReuseAfterFinish() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        extractor.feed("<img>first</img>");
        extractor.finish();

        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("<img>second</img>");
        assertEquals(1, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("second", keywords.get(0).value());
    }

    // ==================== 纯文本 ====================

    @Test
    public void testPlainTextOnly() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> k1 = extractor.feed("hello ");
        assertTrue(k1.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k2 = extractor.feed("world");
        assertTrue(k2.isEmpty());

        List<StreamKeywordsExtractor.Keyword> k3 = extractor.finish();
        assertEquals(1, k3.size());
        assertEquals("text", k3.get(0).type());
        assertEquals("hello world", k3.get(0).value());
    }

    // ==================== Keyword 不可变性 ====================

    @Test
    public void testKeywordAttributesUnmodifiable() {
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("src", "a.jpg");

        StreamKeywordsExtractor.Keyword kw = new StreamKeywordsExtractor.Keyword("img", null, attrs);

        attrs.put("src", "modified.jpg");
        assertEquals("a.jpg", kw.attributes().get("src"));

        try {
            kw.attributes().put("newKey", "newVal");
            // UnsupportedOperationException expected for unmodifiable map
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    public void testKeywordEqualsAndHashCode() {
        StreamKeywordsExtractor.Keyword k1 = new StreamKeywordsExtractor.Keyword("img", "url", null);
        StreamKeywordsExtractor.Keyword k2 = new StreamKeywordsExtractor.Keyword("img", "url", null);

        assertEquals(k1, k2);
        assertEquals(k1.hashCode(), k2.hashCode());
    }

    @Test
    public void testKeywordToString() {
        StreamKeywordsExtractor.Keyword kw = new StreamKeywordsExtractor.Keyword("img", "url", null);
        String s = kw.toString();

        assertTrue(s.contains("img"));
        assertTrue(s.contains("url"));
    }

    // ==================== 边界输入 ====================

    @Test
    public void testNullChunk() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(null);

        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    @Test
    public void testEmptyChunk() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed("");

        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    @Test
    public void testMultipleTagsInSequence() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();
        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "<img src='a.jpg'/><video>clip</video>");

        assertEquals(2, keywords.size());
        assertEquals("img", keywords.get(0).type());
        assertEquals("a.jpg", keywords.get(0).attributes().get("src"));
        assertEquals("video", keywords.get(1).type());
        assertEquals("clip", keywords.get(1).value());
    }

    @Test
    public void testVariableAndTagMixed() {
        StreamKeywordsExtractor extractor = new StreamKeywordsExtractor();

        List<StreamKeywordsExtractor.Keyword> keywords = extractor.feed(
                "%name%<img src='a.jpg'/>%time%");

        assertEquals(3, keywords.size());
        assertEquals("variable", keywords.get(0).type());
        assertEquals("name", keywords.get(0).value());
        assertEquals("img", keywords.get(1).type());
        assertEquals("variable", keywords.get(2).type());
        assertEquals("time", keywords.get(2).value());
    }
}
