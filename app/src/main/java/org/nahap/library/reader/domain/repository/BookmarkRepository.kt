package org.nahap.library.reader.domain.repository

import org.nahap.library.reader.domain.model.Bookmark

/**
 * Domain интерфейс репозитория закладок
 */
interface BookmarkRepository {
    suspend fun getBookmarks(bookId: Int): Result<List<Bookmark>>
    suspend fun createBookmark(bookId: Int, position: Double, name: String, notes: String): Result<Bookmark>
    suspend fun updateBookmark(bookmarkId: Int, position: Double, name: String, notes: String): Result<Bookmark>
    suspend fun deleteBookmark(bookmarkId: Int): Result<Unit>
}
