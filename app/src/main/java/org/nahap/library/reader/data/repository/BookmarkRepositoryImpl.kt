package org.nahap.library.reader.data.repository

import org.nahap.library.reader.api.BookmarkApi
import org.nahap.library.reader.data.mapper.BookmarkMapper
import org.nahap.library.reader.domain.model.Bookmark
import org.nahap.library.reader.domain.repository.BookmarkRepository
import org.nahap.library.reader.model.BookmarkCreateRequest
import org.nahap.library.reader.model.BookmarkUpdateRequest
import javax.inject.Inject

/**
 * Реализация репозитория закладок
 */
class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkApi: BookmarkApi
) : BookmarkRepository {
    
    override suspend fun getBookmarks(bookId: Int): Result<List<Bookmark>> {
        return try {
            val response = bookmarkApi.getBookmarks(bookId)
            val bookmarks = BookmarkMapper.toDomainList(response.content)
            Result.success(bookmarks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createBookmark(
        bookId: Int,
        position: Double,
        name: String,
        notes: String
    ): Result<Bookmark> {
        return try {
            val request = BookmarkCreateRequest(bookId, position, name, notes)
            val response = bookmarkApi.createBookmark(request)
            val bookmark = BookmarkMapper.toDomain(response)
            Result.success(bookmark)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBookmark(
        bookmarkId: Int,
        position: Double,
        name: String,
        notes: String
    ): Result<Bookmark> {
        return try {
            val request = BookmarkUpdateRequest(position, name, notes)
            val response = bookmarkApi.updateBookmark(bookmarkId, request)
            val bookmark = BookmarkMapper.toDomain(response)
            Result.success(bookmark)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteBookmark(bookmarkId: Int): Result<Unit> {
        return try {
            bookmarkApi.deleteBookmark(bookmarkId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
