package org.nahap.library.reader.model


data class ReaderState(
    val bookId: Int,
    val title: String = "",
    val author: String = "",
    val htmlContent: String = "",
    val toc: List<TocItem> = emptyList(),
    val bookmarks: List<BookmarkResponse> = emptyList(),
    val currentPosition: Double = 0.0,
    val currentPage: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val readingSettings: ReadingSettings = ReadingSettings()
)


data class TocItem(
    val label: String,
    val href: String,
    val subitems: List<TocItem>? = null
)