package org.nahap.library.reader.domain.usecase

import org.nahap.library.reader.domain.model.Bookmark
import org.nahap.library.reader.domain.repository.BookmarkRepository
import javax.inject.Inject

/**
 * Use case для обновления закладки
 */
class UpdateBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(
        bookmarkId: Int,
        position: Double,
        name: String,
        notes: String
    ): Result<Bookmark> {
        return bookmarkRepository.updateBookmark(bookmarkId, position, name, notes)
    }
}
