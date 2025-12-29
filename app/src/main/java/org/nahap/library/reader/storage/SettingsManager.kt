package org.nahap.library.reader.storage

import android.content.Context
import android.content.SharedPreferences
import org.nahap.library.reader.model.ReadingSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "reading_settings",
        Context.MODE_PRIVATE
    )

    fun saveSettings(settings: ReadingSettings) {
        prefs.edit().apply {
            putFloat(KEY_FONT_SIZE, settings.fontSize)
            putFloat(KEY_LINE_HEIGHT, settings.lineHeight)
            putFloat(KEY_WORD_SPACING, settings.wordSpacing)
            putInt(KEY_MAX_INLINE_SIZE, settings.maxInlineSize)
            putString(KEY_THEME, settings.theme.name)
            putString(KEY_FLOW, settings.flow.name)
            putBoolean(KEY_JUSTIFY, settings.justify)
            putBoolean(KEY_HYPHENATE, settings.hyphenate)
            apply()
        }
    }

    fun loadSettings(): ReadingSettings {
        return ReadingSettings(
            fontSize = prefs.getFloat(KEY_FONT_SIZE, 18f),
            lineHeight = prefs.getFloat(KEY_LINE_HEIGHT, 1.7f),
            wordSpacing = prefs.getFloat(KEY_WORD_SPACING, 0f),
            maxInlineSize = prefs.getInt(KEY_MAX_INLINE_SIZE, 900),
            theme = ReadingSettings.ReadingTheme.valueOf(
                prefs.getString(KEY_THEME, "LIGHT") ?: "LIGHT"
            ),
            flow = ReadingSettings.ReadingFlow.valueOf(
                prefs.getString(KEY_FLOW, "SCROLLED") ?: "SCROLLED"
            ),
            justify = prefs.getBoolean(KEY_JUSTIFY, true),
            hyphenate = prefs.getBoolean(KEY_HYPHENATE, true)
        )
    }

    fun saveReadingPosition(bookId: Int, position: Double) {
        prefs.edit().putFloat("book_position_$bookId", position.toFloat()).apply()
    }

    fun getReadingPosition(bookId: Int): Double {
        return prefs.getFloat("book_position_$bookId", 0.0f).toDouble()
    }

    companion object {
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_LINE_HEIGHT = "line_height"
        private const val KEY_WORD_SPACING = "word_spacing"
        private const val KEY_MAX_INLINE_SIZE = "max_inline_size"
        private const val KEY_THEME = "theme"
        private const val KEY_FLOW = "flow"
        private const val KEY_JUSTIFY = "justify"
        private const val KEY_HYPHENATE = "hyphenate"
    }
}

