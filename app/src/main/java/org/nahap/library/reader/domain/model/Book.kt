package org.nahap.library.reader.domain.model

/**
 * Domain модель книги
 */
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val htmlContent: String,
    val toc: List<TocItem>
)

/**
 * Domain модель элемента содержания
 */
data class TocItem(
    val label: String,
    val href: String,
    val subitems: List<TocItem>? = null
)
