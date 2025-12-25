package org.nahap.library.reader.parser

import android.util.Log
import org.nahap.library.reader.model.TocItem
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.xml.sax.InputSource

class FB2Parser {

    companion object {
        private const val TAG = "FB2Parser"
        private const val NS_XLINK = "http://www.w3.org/1999/xlink"
    }

    private val binaryImages = mutableMapOf<String, String>()
    private var sectionCounter = 0

    data class ParseResult(
        val title: String,
        val author: String,
        val htmlContent: String,
        val toc: List<TocItem>
    )

    fun parse(fb2Xml: String): ParseResult {
        sectionCounter = 0

        val doc = parseXML(fb2Xml)
        cacheBinaryImages(doc)

        val title = getElementText(doc, "title-info", "book-title")
        val author = parseAuthor(doc)

        val body = doc.getElementsByTagName("body").item(0) as? Element

        val tocItems = mutableListOf<TocItem>()
        val htmlContent = body?.let { convertBody(it, tocItems) } ?: ""

        Log.d(TAG, "Parsed ${tocItems.size} TOC items")
        tocItems.forEachIndexed { index, item ->
            Log.d(TAG, "  TOC[$index]: '${item.label}' -> '${item.href}'")
        }

        return ParseResult(
            title = title,
            author = author,
            htmlContent = wrapHtml(htmlContent),
            toc = tocItems
        )
    }

    private fun parseXML(xml: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        return factory.newDocumentBuilder().parse(InputSource(StringReader(xml)))
    }

    private fun cacheBinaryImages(doc: Document) {
        val binaries = doc.getElementsByTagName("binary")
        for (i in 0 until binaries.length) {
            val binary = binaries.item(i) as Element
            val id = binary.getAttribute("id")
            val contentType = binary.getAttribute("content-type") ?: "image/jpeg"
            val data = binary.textContent.replace("\\s".toRegex(), "")
            binaryImages[id] = "data:$contentType;base64,$data"
        }
    }

    private fun getElementText(doc: Document, vararg path: String): String {
        var elements: org.w3c.dom.NodeList? = null
        for (tag in path) {
            elements = if (elements == null) {
                doc.getElementsByTagName(tag)
            } else {
                (elements.item(0) as? Element)?.getElementsByTagName(tag)
            }
            if (elements == null || elements.length == 0) return ""
        }
        return elements?.item(0)?.textContent?.trim() ?: ""
    }

    private fun parseAuthor(doc: Document): String {
        val authorEl = doc.getElementsByTagName("title-info")
            .item(0)?.let { it as Element }
            ?.getElementsByTagName("author")?.item(0) as? Element
            ?: return ""

        val first = authorEl.getElementsByTagName("first-name").item(0)?.textContent?.trim() ?: ""
        val last = authorEl.getElementsByTagName("last-name").item(0)?.textContent?.trim() ?: ""
        val middle = authorEl.getElementsByTagName("middle-name").item(0)?.textContent?.trim() ?: ""

        return listOf(first, middle, last).filter { it.isNotEmpty() }.joinToString(" ")
    }

    private fun convertBody(body: Element, tocItems: MutableList<TocItem>): String {
        val html = StringBuilder()
        val children = body.childNodes

        for (i in 0 until children.length) {
            val node = children.item(i)
            if (node is Element && node.tagName == "section") {
                convertSection(node, html, tocItems, isTopLevel = true)
            }
        }

        return html.toString()
    }

    private fun convertSection(
        section: Element,
        html: StringBuilder,
        tocItems: MutableList<TocItem>,
        isTopLevel: Boolean
    ) {
        val sectionId = "section-${sectionCounter++}"

        val titleText = extractSectionTitle(section)

        if (isTopLevel && titleText.isNotEmpty()) {
            tocItems.add(TocItem(titleText, sectionId))
            Log.d(TAG, "Added TOC item: '$titleText' -> '$sectionId'")
        }

        html.append("<section id=\"$sectionId\">")

        processChildren(section, html, tocItems)

        html.append("</section>")
    }

    private fun extractSectionTitle(section: Element): String {
        val children = section.childNodes
        for (i in 0 until children.length) {
            val child = children.item(i)
            if (child is Element && child.tagName == "title") {
                return child.textContent?.trim()?.replace("\\s+".toRegex(), " ") ?: ""
            }
        }

        for (i in 0 until children.length) {
            val child = children.item(i)
            if (child is Element && child.tagName == "p") {
                val text = child.textContent?.trim() ?: ""
                return if (text.length > 50) text.take(47) + "..." else text
            }
        }

        return ""
    }

    private fun processChildren(parent: Element, html: StringBuilder, tocItems: MutableList<TocItem>) {
        val children = parent.childNodes

        for (i in 0 until children.length) {
            when (val child = children.item(i)) {
                is Element -> processElement(child, html, tocItems)
                is Text -> {
                    val text = child.textContent.trim()
                    if (text.isNotEmpty()) html.append(escape(text))
                }
            }
        }
    }

    private fun processElement(el: Element, html: StringBuilder, tocItems: MutableList<TocItem>) {
        when (el.tagName) {
            "title" -> {
                html.append("<div class=\"title\">")
                processChildren(el, html, tocItems)
                html.append("</div>")
            }
            "p" -> {
                val id = el.getAttribute("id")
                html.append("<p")
                if (id.isNotEmpty()) html.append(" id=\"$id\"")
                html.append(">")
                processInline(el, html)
                html.append("</p>")
            }
            "empty-line" -> html.append("<br/>")
            "subtitle" -> {
                html.append("<h2>")
                processInline(el, html)
                html.append("</h2>")
            }
            "epigraph", "cite" -> {
                html.append("<blockquote>")
                processChildren(el, html, tocItems)
                html.append("</blockquote>")
            }
            "poem" -> {
                html.append("<blockquote class=\"poem\">")
                processChildren(el, html, tocItems)
                html.append("</blockquote>")
            }
            "stanza" -> {
                val verses = el.getElementsByTagName("v")
                for (i in 0 until verses.length) {
                    html.append("<p>${escape(verses.item(i).textContent)}</p>")
                }
            }
            "image" -> {
                val href = el.getAttributeNS(NS_XLINK, "href").removePrefix("#")
                val src = binaryImages[href] ?: href
                html.append("<img src=\"$src\" />")
            }
            "section" -> {
                convertSection(el, html, tocItems, isTopLevel = false)
            }
            "text-author" -> {
                html.append("<p class=\"text-author\">")
                processInline(el, html)
                html.append("</p>")
            }
            else -> processChildren(el, html, tocItems)
        }
    }

    private fun processInline(el: Element, html: StringBuilder) {
        val children = el.childNodes
        for (i in 0 until children.length) {
            when (val child = children.item(i)) {
                is Element -> when (child.tagName) {
                    "strong" -> {
                        html.append("<strong>")
                        processInline(child, html)
                        html.append("</strong>")
                    }
                    "emphasis" -> {
                        html.append("<em>")
                        processInline(child, html)
                        html.append("</em>")
                    }
                    "strikethrough" -> {
                        html.append("<s>")
                        processInline(child, html)
                        html.append("</s>")
                    }
                    "sub" -> {
                        html.append("<sub>")
                        processInline(child, html)
                        html.append("</sub>")
                    }
                    "sup" -> {
                        html.append("<sup>")
                        processInline(child, html)
                        html.append("</sup>")
                    }
                    "a" -> {
                        val href = child.getAttributeNS(NS_XLINK, "href")
                        html.append("<a href=\"$href\">")
                        processInline(child, html)
                        html.append("</a>")
                    }
                    else -> processInline(child, html)
                }
                is Text -> html.append(escape(child.textContent))
            }
        }
    }

    private fun wrapHtml(content: String) = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <style>
        * { 
            -webkit-tap-highlight-color: transparent;
            box-sizing: border-box;
        }
        html, body {
            margin: 0;
            padding: 0;
        }
        body {
            font-family: 'Roboto', 'Noto Serif', serif;
            font-size: 18px;
            line-height: 1.7;
            padding: 16px;
            background: #FFFFFF;
            color: #000000;
        }
        section {
            scroll-margin-top: 20px;
        }
        
        h1, h2, h3 { 
            font-weight: 700;
            margin: 1.5em 0 0.5em;
            color: #000000;
        }
        h1 { font-size: 1.6em; }
        h2 { font-size: 1.4em; }
        h3 { font-size: 1.2em; }
        
        .title { 
            text-align: center; 
            margin: 2em 0 1.5em; 
            font-weight: 800;
            font-size: 1.5em;
            color: #000000;
            line-height: 1.3;
        }
        
        .subtitle {
            text-align: center;
            margin: 1em 0;
            font-weight: 600;
            font-size: 1.2em;
            font-style: italic;
        }
        
        p { 
            margin: 0.8em 0; 
            text-align: justify; 
            text-indent: 1.5em; 
        }
        p:first-of-type, 
        .title + p, 
        h1 + p, 
        h2 + p,
        h3 + p { 
            text-indent: 0; 
        }
        blockquote { 
            margin: 1em 2em; 
            font-style: italic; 
            border-left: 3px solid #CCCCCC;
            padding-left: 1em;
        }
        img { 
            max-width: 100%; 
            height: auto; 
            display: block; 
            margin: 1em auto; 
        }
        strong { font-weight: bold; }
        em { font-style: italic; }
        .poem p { 
            text-indent: 0; 
            margin-left: 2em; 
        }
        .text-author {
            text-align: right;
            font-style: italic;
            margin-top: 1em;
        }
        .text-author:before {
            content: "— ";
        }
        
        .epigraph {
            margin: 1.5em 2em;
            font-style: italic;
            font-size: 0.95em;
        }
    </style>
</head>
<body>
$content
</body>
</html>
""".trimIndent()

    private fun escape(text: String) = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}