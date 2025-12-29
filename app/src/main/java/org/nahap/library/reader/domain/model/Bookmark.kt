package org.nahap.library.reader.domain.model

/**
 * Domain модель закладки
 */
data class Bookmark(
    val id: Int,
    val bookId: Int,
    val position: Double,
    val name: String,
    val notes: String,
    val createdAt: String?
)
