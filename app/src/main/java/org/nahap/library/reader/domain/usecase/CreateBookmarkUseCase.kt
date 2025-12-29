package org.nahap.library.reader.domain.usecase

import org.nahap.library.reader.domain.model.Bookmark
import org.nahap.library.reader.domain.repository.BookmarkRepository
import javax.inject.Inject

/**
 * Use case для создания закладки
 */
class CreateBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(
        bookId: Int,
        position: Double,
        name: String,
        notes: String = ""
    ): Result<Bookmark> {
        return bookmarkRepository.createBookmark(bookId, position, name, notes)
    }
}
