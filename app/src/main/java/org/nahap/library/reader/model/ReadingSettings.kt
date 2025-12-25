package org.nahap.library.reader.model


data class ReadingSettings(
    val fontSize: Float = 18f,
    val lineHeight: Float = 1.7f,
    val wordSpacing: Float = 0f,
    val maxInlineSize: Int = 900,
    val theme: ReadingTheme = ReadingTheme.LIGHT,
    val flow: ReadingFlow = ReadingFlow.SCROLLED,
    val justify: Boolean = true,
    val hyphenate: Boolean = true
) {


    enum class ReadingTheme(val displayName: String) {
        LIGHT("Светлая"),
        DARK("Темная")
    }


    enum class ReadingFlow(val displayName: String) {
        SCROLLED("Прокрутка"),
        PAGINATED("Страницы")
    }


    fun toCss(): String {
        val backgroundColor = when (theme) {
            ReadingTheme.LIGHT -> "#FFFFFF"
            ReadingTheme.DARK -> "#1E1E1E"
        }

        val textColor = when (theme) {
            ReadingTheme.LIGHT -> "#000000"
            ReadingTheme.DARK -> "#E0E0E0"
        }

        val linkColor = when (theme) {
            ReadingTheme.LIGHT -> "#1976D2"
            ReadingTheme.DARK -> "#64B5F6"
        }

        val textAlign = if (justify) "justify" else "left"
        val hyphens = if (hyphenate) "auto" else "manual"

        return """
            body {
                font-size: ${fontSize}px !important;
                line-height: ${lineHeight} !important;
                word-spacing: ${wordSpacing}em !important;
                background-color: $backgroundColor !important;
                color: $textColor !important;
                max-width: ${maxInlineSize}px !important;
                margin: 0 auto !important;
            }
            
            p, li, blockquote, dd {
                line-height: ${lineHeight} !important;
                text-align: $textAlign !important;
                -webkit-hyphens: $hyphens !important;
                hyphens: $hyphens !important;
                word-spacing: ${wordSpacing}em !important;
            }
            
            h1, h2, h3, .title {
                color: $textColor !important;
            }
            
            a {
                color: $linkColor !important;
            }
        """.trimIndent()
    }
}