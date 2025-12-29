package org.nahap.library.reader.domain.usecase

import org.nahap.library.reader.domain.model.Bookmark
import org.nahap.library.reader.domain.repository.BookmarkRepository
import javax.inject.Inject

/**
 * Use case для получения закладок книги
 */
class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(bookId: Int): Result<List<Bookmark>> {
        return bookmarkRepository.getBookmarks(bookId)
    }
}
