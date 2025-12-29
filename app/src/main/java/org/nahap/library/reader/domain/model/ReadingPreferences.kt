package org.nahap.library.reader.domain.model

/**
 * Domain модель настроек чтения
 */
data class ReadingPreferences(
    val fontSize: Float = 18f,
    val lineHeight: Float = 1.7f,
    val wordSpacing: Float = 0f,
    val maxInlineSize: Int = 900,
    val theme: ReadingTheme = ReadingTheme.LIGHT,
    val flow: ReadingFlow = ReadingFlow.SCROLLED,
    val justify: Boolean = true,
    val hyphenate: Boolean = true
) {
    enum class ReadingTheme {
        LIGHT,
        DARK
    }

    enum class ReadingFlow {
        SCROLLED,
        PAGINATED
    }
}
