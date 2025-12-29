package org.nahap.library.reader.domain.usecase

import org.nahap.library.reader.domain.model.Book
import org.nahap.library.reader.domain.repository.BookRepository
import javax.inject.Inject

/**
 * Use case для загрузки книги
 */
class LoadBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Int): Result<Book> {
        return bookRepository.getBook(bookId)
    }
}
