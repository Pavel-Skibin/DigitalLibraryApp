package org.nahap.library.reader.data.mapper

import org.nahap.library.reader.domain.model.Bookmark
import org.nahap.library.reader.model.BookmarkResponse

/**
 * Mapper для преобразования API моделей закладок в Domain модели
 */
object BookmarkMapper {
    
    fun toDomain(response: BookmarkResponse): Bookmark {
        return Bookmark(
            id = response.id,
            bookId = response.bookId,
            position = response.position,
            name = response.name,
            notes = response.notes ?: "",
            createdAt = response.createdAt
        )
    }
    
    fun toDomainList(responses: List<BookmarkResponse>): List<Bookmark> {
        return responses.map { toDomain(it) }
    }
}
