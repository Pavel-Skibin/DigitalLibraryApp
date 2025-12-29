package org.nahap.library.reader.domain.usecase

import org.nahap.library.reader.domain.repository.BookmarkRepository
import javax.inject.Inject

/**
 * Use case для удаления закладки
 */
class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(bookmarkId: Int): Result<Unit> {
        return bookmarkRepository.deleteBookmark(bookmarkId)
    }
}
