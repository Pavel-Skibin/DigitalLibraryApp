package org.nahap.library.reader.data.repository

import org.nahap.library.reader.api.BookApi
import org.nahap.library.reader.data.mapper.BookMapper
import org.nahap.library.reader.domain.model.Book
import org.nahap.library.reader.domain.repository.BookRepository
import org.nahap.library.reader.parser.FB2Parser
import javax.inject.Inject

/**
 * Реализация репозитория книг
 */
class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi,
    private val fb2Parser: FB2Parser
) : BookRepository {
    
    override suspend fun getBook(bookId: Int): Result<Book> {
        return try {
            val fb2Content = bookApi.getBookFb2(bookId)
            val parseResult = fb2Parser.parse(fb2Content)
            val book = BookMapper.toDomain(parseResult, bookId)
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
