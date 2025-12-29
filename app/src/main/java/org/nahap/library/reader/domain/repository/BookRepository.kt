package org.nahap.library.reader.domain.repository

import org.nahap.library.reader.domain.model.Book

/**
 * Domain интерфейс репозитория книг
 */
interface BookRepository {
    suspend fun getBook(bookId: Int): Result<Book>
}
