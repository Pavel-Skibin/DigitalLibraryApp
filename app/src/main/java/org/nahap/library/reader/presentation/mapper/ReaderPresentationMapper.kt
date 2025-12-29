package org.nahap.library.reader.presentation.mapper

import org.nahap.library.reader.domain.model.Book
import org.nahap.library.reader.domain.model.Bookmark
import org.nahap.library.reader.domain.model.ReadingPreferences
import org.nahap.library.reader.domain.model.TocItem as DomainTocItem
import org.nahap.library.reader.model.BookmarkResponse
import org.nahap.library.reader.model.ReadingSettings
import org.nahap.library.reader.model.TocItem as UiTocItem

/**
 * Mapper для преобразования Domain моделей в UI модели (Presentation слой)
 */
object ReaderPresentationMapper {
    
    fun tocItemToUi(domain: DomainTocItem): UiTocItem {
        return UiTocItem(
            label = domain.label,
            href = domain.href,
            subitems = domain.subitems?.map { tocItemToUi(it) }
        )
    }
    
    fun bookmarkToUi(domain: Bookmark): BookmarkResponse {
        return BookmarkResponse(
            id = domain.id,
            bookId = domain.bookId,
            position = domain.position,
            name = domain.name,
            notes = domain.notes,
            createdAt = domain.createdAt
        )
    }
    
    fun readingPreferencesToUi(domain: ReadingPreferences): ReadingSettings {
        return ReadingSettings(
            fontSize = domain.fontSize,
            lineHeight = domain.lineHeight,
            wordSpacing = domain.wordSpacing,
            maxInlineSize = domain.maxInlineSize,
            theme = when (domain.theme) {
                ReadingPreferences.ReadingTheme.LIGHT -> ReadingSettings.ReadingTheme.LIGHT
                ReadingPreferences.ReadingTheme.DARK -> ReadingSettings.ReadingTheme.DARK
            },
            flow = when (domain.flow) {
                ReadingPreferences.ReadingFlow.SCROLLED -> ReadingSettings.ReadingFlow.SCROLLED
                ReadingPreferences.ReadingFlow.PAGINATED -> ReadingSettings.ReadingFlow.PAGINATED
            },
            justify = domain.justify,
            hyphenate = domain.hyphenate
        )
    }
    
    fun readingSettingsToDomain(ui: ReadingSettings): ReadingPreferences {
        return ReadingPreferences(
            fontSize = ui.fontSize,
            lineHeight = ui.lineHeight,
            wordSpacing = ui.wordSpacing,
            maxInlineSize = ui.maxInlineSize,
            theme = when (ui.theme) {
                ReadingSettings.ReadingTheme.LIGHT -> ReadingPreferences.ReadingTheme.LIGHT
                ReadingSettings.ReadingTheme.DARK -> ReadingPreferences.ReadingTheme.DARK
            },
            flow = when (ui.flow) {
                ReadingSettings.ReadingFlow.SCROLLED -> ReadingPreferences.ReadingFlow.SCROLLED
                ReadingSettings.ReadingFlow.PAGINATED -> ReadingPreferences.ReadingFlow.PAGINATED
            },
            justify = ui.justify,
            hyphenate = ui.hyphenate
        )
    }
}
