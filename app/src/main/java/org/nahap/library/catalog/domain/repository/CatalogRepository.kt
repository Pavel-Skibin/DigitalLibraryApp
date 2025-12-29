package org.nahap.library.catalog.domain.repository

import org.nahap.library.catalog.domain.model.Author
import org.nahap.library.catalog.domain.model.BookDetail
import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.model.Genre
import org.nahap.library.catalog.domain.model.Page
import org.nahap.library.catalog.domain.model.SearchFilter

/**
 * Domain интерфейс репозитория каталога
 */
interface CatalogRepository {
    suspend fun searchBooks(filter: SearchFilter, page: Int): Result<Page<CatalogBook>>
    suspend fun getTopRatedBooks(page: Int): Result<List<CatalogBook>>
    suspend fun getBooksByGenre(genreId: Int, page: Int): Result<Page<CatalogBook>>
    suspend fun getBookDetail(bookId: Int): Result<BookDetail>
    suspend fun getAllGenres(): Result<List<Genre>>
    suspend fun getAllAuthors(): Result<List<Author>>
    fun getBookCoverUrl(bookId: Int): String
}
