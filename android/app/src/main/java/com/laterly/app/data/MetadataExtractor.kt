package com.laterly.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

data class LinkMetadata(
    val url: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val articleContent: String = "",
    val author: String = "",
    val estimatedReadMinutes: Int = 3,
    val wordCount: Int = 0
)

/**
 * Robust on-device link extraction engine:
 * 1. Correct encoding handling: Favors UTF-8, resolves declared meta charsets, and repairs mojibake.
 * 2. Complete entity cleaning: Decodes multi-level HTML/numeric entities without mangling valid Unicode/Hindi.
 * 3. Sanitized content extraction: Strips scripts, styles, SVGs, JSON payloads, and tracking noise.
 */
object MetadataExtractor {

    suspend fun extract(rawUrl: String): LinkMetadata = withContext(Dispatchers.IO) {
        val targetUrl = sanitizeUrl(rawUrl)
        try {
            val response = Jsoup.connect(targetUrl)
                .userAgent("Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36")
                .timeout(8000)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .execute()

            val rawBytes = response.bodyAsBytes()
            val doc = parseDocumentWithAccurateEncoding(rawBytes, targetUrl, response.charset())

            // 1. Resolve Final Canonical or Target URL
            val canonicalUrl = doc.select("link[rel=canonical]").attr("abs:href").takeIf { it.isNotBlank() } ?: targetUrl

            // 2. Title extraction: og:title -> twitter:title -> meta[name=title] -> <title>
            val rawTitle = doc.select("meta[property=og:title]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=twitter:title]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=title]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[property=title]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.title().takeIf { it.isNotBlank() }
                ?: targetUrl

            // 3. Description extraction: og:description -> twitter:description -> meta[name=description]
            val rawDescription = doc.select("meta[property=og:description]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=twitter:description]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=description]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[property=description]").attr("content").takeIf { it.isNotBlank() }

            // 4. Image extraction with relative URL resolution: og:image -> twitter:image -> apple-touch-icon -> first large img
            val rawImage = doc.select("meta[property=og:image:secure_url]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[property=og:image]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=twitter:image:src]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=twitter:image]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("link[rel=apple-touch-icon]").attr("href").takeIf { it.isNotBlank() }
                ?: doc.select("link[rel=image_src]").attr("href").takeIf { it.isNotBlank() }
                ?: doc.select("article img[src], main img[src], .post img[src]").firstOrNull()?.attr("src")

            val imageUrl = resolveAbsoluteUrl(canonicalUrl, rawImage)

            // 5. Author extraction: meta[name=author] -> article:author -> twitter:creator
            val rawAuthor = doc.select("meta[name=author]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[property=article:author]").attr("content").takeIf { it.isNotBlank() }
                ?: doc.select("meta[name=twitter:creator]").attr("content").takeIf { it.isNotBlank() }
                ?: ""

            // 6. Clean Article Content Extraction (for Reader Mode & future AI summarizer)
            val articleContent = extractReadableContent(doc)

            val cleanedTitle = sanitizeText(rawTitle)
            var cleanedDescription = rawDescription?.let { sanitizeText(it) }?.takeIf { it.isNotBlank() }

            // If description is empty or too short, generate a preview snippet from the clean article content
            if (cleanedDescription.isNullOrBlank() && articleContent.isNotBlank()) {
                cleanedDescription = articleContent.lineSequence()
                    .map { it.trim() }
                    .filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("•") }
                    .firstOrNull()
                    ?.take(200)
            }

            // 7. Word Count & Read Time Calculation
            val textForCount = if (articleContent.isNotBlank()) articleContent else (cleanedDescription ?: "")
            val wordCount = textForCount.split("\\s+".toRegex()).count { it.isNotBlank() }
            val readMinutes = (wordCount / 200).coerceAtLeast(1)

            LinkMetadata(
                url = targetUrl,
                title = cleanedTitle,
                description = cleanedDescription,
                imageUrl = imageUrl,
                articleContent = articleContent,
                author = sanitizeText(rawAuthor),
                estimatedReadMinutes = readMinutes,
                wordCount = wordCount
            )
        } catch (_: Exception) {
            LinkMetadata(
                url = targetUrl,
                title = targetUrl,
                description = null,
                imageUrl = null,
                articleContent = "",
                author = "",
                estimatedReadMinutes = 1,
                wordCount = 0
            )
        }
    }

    /**
     * Accurately parses HTML bytes by honoring UTF-8 first, checking embedded meta charset,
     * and falling back gracefully without defaulting to ISO-8859-1 mojibake.
     */
    private fun parseDocumentWithAccurateEncoding(rawBytes: ByteArray, baseUri: String, headerCharset: String?): Document {
        // Try UTF-8 first
        var doc = Jsoup.parse(ByteArrayInputStream(rawBytes), "UTF-8", baseUri)

        // Check if the HTML explicitly declares an encoding in <meta charset> or <meta http-equiv>
        val metaElement = doc.selectFirst("meta[charset], meta[http-equiv=content-type]")
        var detectedCharset: String? = null
        if (metaElement != null) {
            if (metaElement.hasAttr("charset")) {
                detectedCharset = metaElement.attr("charset")
            } else if (metaElement.hasAttr("content")) {
                val content = metaElement.attr("content")
                val idx = content.indexOf("charset=")
                if (idx != -1) {
                    detectedCharset = content.substring(idx + 8).trim().replace("\"", "").replace("'", "")
                }
            }
        }

        // If a non-UTF-8 charset is explicitly declared and supported, reparse with it
        val effectiveCharset = detectedCharset ?: headerCharset
        if (!effectiveCharset.isNullOrBlank() && !effectiveCharset.equals("UTF-8", ignoreCase = true)
            && !effectiveCharset.equals("ISO-8859-1", ignoreCase = true)) {
            try {
                if (Charset.isSupported(effectiveCharset)) {
                    doc = Jsoup.parse(ByteArrayInputStream(rawBytes), effectiveCharset, baseUri)
                }
            } catch (_: Exception) {
                // Keep the UTF-8 parsed document
            }
        }

        return doc
    }

    /**
     * Sanitizes extracted text:
     * - Unescapes all numeric and named HTML entities (iterative).
     * - Cleans up non-breaking spaces and zero-width artifacts.
     * - Repairs mojibake (UTF-8 bytes misdecoded as Windows-1252/ISO-8859-1).
     * - Strips leftover HTML tags.
     * - Preserves valid Unicode and Hindi / non-Latin scripts.
     */
    fun sanitizeText(input: String): String {
        if (input.isBlank()) return ""

        var text = input

        // 1. Strip raw HTML tags if any leaked into metadata
        if (text.contains('<') && text.contains('>')) {
            text = Jsoup.parse(text).text()
        }

        // 2. Iterative HTML entity unescaping (&amp;#8217; -> &#8217; -> ’)
        for (i in 0 until 3) {
            val unescaped = Parser.unescapeEntities(text, false)
            if (unescaped == text) break
            text = unescaped
        }

        // 3. Replace non-breaking / zero-width whitespace
        text = text.replace('\u00A0', ' ')
            .replace('\u200B', ' ')
            .replace('\uFEFF', ' ')

        // 4. Detect and repair mojibake if UTF-8 was misinterpreted as Windows-1252 / ISO-8859-1
        if (containsMojibake(text)) {
            try {
                val win1252 = Charset.forName("windows-1252")
                val candidate = String(text.toByteArray(win1252), StandardCharsets.UTF_8)
                if (!candidate.contains('\uFFFD') && !containsMojibake(candidate)) {
                    text = candidate
                }
            } catch (_: Exception) {
                // Fallback to original text
            }
        }

        // 5. Normalize whitespace
        return text.replace("\\s+".toRegex(), " ").trim()
    }

    private fun containsMojibake(s: String): Boolean {
        return s.contains("â€™") || s.contains("â€œ") || s.contains("â€") ||
               s.contains("â€”") || s.contains("â€“") || s.contains("â€¦") ||
               s.contains("Ã©") || s.contains("Ã") || s.contains("Â ") ||
               s.contains("à¤") || s.contains("à¥")
    }

    /**
     * Resolves relative image URLs against the base URL (handling leading slashes, scheme-relative, etc.)
     */
    private fun resolveAbsoluteUrl(baseUrl: String, candidate: String?): String? {
        if (candidate.isNullOrBlank()) return null
        val trimmed = candidate.trim()
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }
        if (trimmed.startsWith("//")) {
            return "https:$trimmed"
        }
        return try {
            val baseUri = URI(baseUrl)
            baseUri.resolve(trimmed).toString()
        } catch (_: Exception) {
            trimmed
        }
    }

    /**
     * Strips chrome/boilerplate elements and extracts clean, readable article paragraphs.
     * Filters out script code, CSS rules, SVG path coordinates, tracking tokens, and boilerplate.
     */
    fun extractReadableContent(doc: Document): String {
        val clone = doc.clone()
        clone.select("script, style, noscript, nav, header, footer, aside, form, svg, iframe, .ads, .advertisement, [role=banner], [role=navigation]").remove()

        val mainCandidate = clone.select("article, main, [role=main], .post-content, .entry-content, .article-content, #content, .content").firstOrNull()
        val targetElement = mainCandidate ?: clone.body() ?: return ""

        val paragraphs = targetElement.select("p, h1, h2, h3, h4, blockquote, li")
        if (paragraphs.isNotEmpty()) {
            val sb = StringBuilder()
            for (p in paragraphs) {
                val rawText = p.text().trim()
                if (rawText.length > 20 && !isGarbageText(rawText)) {
                    val cleanP = sanitizeText(rawText)
                    if (cleanP.length > 20) {
                        when {
                            p.tagName().startsWith("h") -> sb.append("\n\n### ").append(cleanP).append("\n\n")
                            p.tagName() == "blockquote" -> sb.append("\n> ").append(cleanP).append("\n\n")
                            p.tagName() == "li" -> sb.append("• ").append(cleanP).append("\n")
                            else -> sb.append(cleanP).append("\n\n")
                        }
                    }
                }
            }
            val result = sb.toString().trim()
            if (result.length > 80) return result
        }

        val fallback = targetElement.text().trim()
        return if (!isGarbageText(fallback)) sanitizeText(fallback) else ""
    }

    /**
     * Identifies code blocks, CSS snippets, JSON blobs, SVG paths, or tracking tokens
     * that occasionally leak into DOM paragraph elements.
     */
    fun isGarbageText(text: String): Boolean {
        if (text.length < 15) return true
        val trimmed = text.trim()

        // 1. Script / Code / JSON patterns
        if (trimmed.startsWith("var ") || trimmed.startsWith("let ") || trimmed.startsWith("const ")
            || trimmed.startsWith("function(") || trimmed.startsWith("window.") || trimmed.startsWith("document.")
            || (trimmed.startsWith("{") && trimmed.endsWith("}"))
            || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return true
        }

        // 2. CSS blocks
        if ((trimmed.startsWith(".") || trimmed.startsWith("#")) && trimmed.contains("{") && trimmed.contains("}")) {
            if (trimmed.contains("display:") || trimmed.contains("margin:") || trimmed.contains("color:") || trimmed.contains("padding:")) {
                return true
            }
        }

        // 3. SVG path coordinate strings (e.g. M10 10 H 90 V 90 H 10 Z)
        if (trimmed.startsWith("M") && trimmed.contains("Z") && trimmed.matches("^[MmLlHhVvCcSsQqTtAaZz0-9,\\.\\-\\s]+$".toRegex())) {
            return true
        }

        // 4. Excessive code / escape syntax ratio
        var codeSymbols = 0
        for (c in trimmed) {
            if (c == '{' || c == '}' || c == ';' || c == '=' || c == '<' || c == '>' || c == '\\' || c == '$') {
                codeSymbols++
            }
        }
        if (trimmed.length > 30 && (codeSymbols.toDouble() / trimmed.length) > 0.15) {
            return true
        }

        return false
    }

    private fun sanitizeUrl(input: String): String {
        val trimmed = input.trim()
        val urlRegex = Regex("""https?://[^\s]+""")
        val match = urlRegex.find(trimmed)
        return match?.value ?: if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }
}
