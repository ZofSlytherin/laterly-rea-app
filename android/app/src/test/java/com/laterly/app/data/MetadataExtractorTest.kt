package com.laterly.app.data

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MetadataExtractorTest {

    @Test
    fun testSanitizeTextDecodesDoubleEntitiesAndQuotes() {
        val input = "Tech &amp;#8217;s Future &amp;amp; AI &amp;mdash; Here&apos;s &quot;Why&quot;"
        val cleaned = MetadataExtractor.sanitizeText(input)
        assertEquals("Tech ’s Future & AI — Here's \"Why\"", cleaned)
    }

    @Test
    fun testSanitizeTextPreservesValidHindiUnicode() {
        val hindi = "भारत और हिंदी समाचार - ‘विशेष’ “रिपोर्ट” — 2026"
        val cleaned = MetadataExtractor.sanitizeText(hindi)
        assertEquals("भारत और हिंदी समाचार - ‘विशेष’ “रिपोर्ट” — 2026", cleaned)
    }

    @Test
    fun testSanitizeTextRepairsMojibakeHindi() {
        // UTF-8 bytes for "हिंदी" misdecoded as Windows-1252 / ISO-8859-1
        val mojibake = "à¤¹à¤¿à¤‚à¤¦à¥€ à¤¸à¤®à¤¾à¤šà¤¾à¤°"
        val cleaned = MetadataExtractor.sanitizeText(mojibake)
        assertEquals("हिंदी समाचार", cleaned)
    }

    @Test
    fun testSanitizeTextCleansNonBreakingAndZeroWidthSpaces() {
        val input = "Hello\u00A0World\u200B\uFEFFWith   Spaces"
        val cleaned = MetadataExtractor.sanitizeText(input)
        assertEquals("Hello World With Spaces", cleaned)
    }

    @Test
    fun testIsGarbageTextFiltersOutScriptsAndCssAndSvg() {
        assertTrue(MetadataExtractor.isGarbageText("window.__PRELOADED_STATE__ = {\"token\":\"abc\"};"))
        assertTrue(MetadataExtractor.isGarbageText(".ad-banner { display: none; margin: 0; color: #000; }"))
        assertTrue(MetadataExtractor.isGarbageText("M10 10 H 90 V 90 H 10 Z"))
        assertTrue(MetadataExtractor.isGarbageText("function(e,t){var n=e.length;for(;n--;)if(e[n]===t)return n;return-1}"))

        assertFalse(MetadataExtractor.isGarbageText("This is a clean, readable paragraph discussing modern technology and design."))
        assertFalse(MetadataExtractor.isGarbageText("भारत में डिजिटल तकनीक और नवाचार का नया अध्याय शुरू हो चुका है।"))
    }

    @Test
    fun testExtractReadableContentStripsNoiseAndKeepsArticle() {
        val html = """
            <!DOCTYPE html>
            <html>
            <body>
                <header><nav><a href="/">Home</a></nav></header>
                <article>
                    <h1>Clean Architecture &amp; Design</h1>
                    <p>window.__DATA__ = {"key":"val"};</p>
                    <p>Clean architecture allows software to remain maintainable over decades by isolating domain rules.</p>
                    <blockquote>"Software architecture is the art of drawing lines that keep modules decoupled."</blockquote>
                    <p><style>.hide{display:none;}</style>Here is the concluding paragraph with clear takeaways for engineers.</p>
                </article>
                <footer><p>&copy; 2026 Company</p></footer>
            </body>
            </html>
        """.trimIndent()

        val doc = Jsoup.parse(html)
        val content = MetadataExtractor.extractReadableContent(doc)

        assertTrue(content.contains("Clean Architecture & Design"))
        assertTrue(content.contains("isolating domain rules"))
        assertTrue(content.contains("Software architecture is the art of drawing lines"))
        assertFalse(content.contains("window.__DATA__"))
        assertFalse(content.contains(".hide{display:none;}"))
    }
}
