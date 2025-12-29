package org.nahap.library.catalog.data.repository

import android.util.Log
import org.nahap.library.catalog.api.AuthorApi
import org.nahap.library.catalog.api.GenreApi
import org.nahap.library.catalog.api.LibraryApi
import org.nahap.library.catalog.data.mapper.CatalogMapper
import org.nahap.library.catalog.domain.model.Author
import org.nahap.library.catalog.domain.model.BookDetail
import org.nahap.library.catalog.domain.model.CatalogBook
import org.nahap.library.catalog.domain.model.Genre
import org.nahap.library.catalog.domain.model.Page
import org.nahap.library.catalog.domain.model.SearchFilter
import org.nahap.library.catalog.domain.repository.CatalogRepository
import org.nahap.library.di.NetworkModule
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация репозитория каталога
 */
@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val libraryApi: LibraryApi,
    private val genreApi: GenreApi,
    private val authorApi: AuthorApi
) : CatalogRepository {
    
    companion object {
        private const val TAG = "CatalogRepositoryImpl"
        private const val PAGE_SIZE = 10
    }
    
    override suspend fun searchBooks(filter: SearchFilter, page: Int): Result<Page<CatalogBook>> {
        return try {
            Log.d(TAG, "Searching books with filter, page $page")
            val response = libraryApi.searchBooks(
                title = filter.title.ifEmpty { "" },
                authorIds = filter.authorIds.ifEmpty { null },
                genreIds = filter.genreIds.ifEmpty { null },
                minRating = filter.minRating,
                maxRating = filter.maxRating,
                page = page,
                size = PAGE_SIZE,
                sort = filter.sortBy.value
            )
            Result.success(CatalogMapper.pageToDomain(response, CatalogMapper::bookToDomain))
        } catch (e: Exception) {
            Log.e(TAG, "Error searching books", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getTopRatedBooks(page: Int): Result<List<CatalogBook>> {
        return try {
            Log.d(TAG, "Loading top rated books, page $page")
            val response = libraryApi.getTopRatedBooks(
                minRatings = 3,
                page = page,
                size = PAGE_SIZE
            )
            val books = response.content.map { CatalogMapper.bookStatToDomain(it) }
            Result.success(books)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading top rated books", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getBooksByGenre(genreId: Int, page: Int): Result<Page<CatalogBook>> {
        return try {
            Log.d(TAG, "Loading books for genre $genreId, page $page")
            val response = libraryApi.searchBooks(
                title = "",
                genreIds = listOf(genreId),
                page = page,
                size = PAGE_SIZE,
                sort = "title,asc"
            )
            Result.success(CatalogMapper.pageToDomain(response, CatalogMapper::bookToDomain))
        } catch (e: Exception) {
            Log.e(TAG, "Error loading books for genre $genreId", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getBookDetail(bookId: Int): Result<BookDetail> {
        return try {
            Log.d(TAG, "Loading book details for ID $bookId")
            val response = libraryApi.getBookDetails(bookId)
            Result.success(CatalogMapper.bookDetailToDomain(response))
        } catch (e: Exception) {
            Log.e(TAG, "Error loading book details for ID $bookId", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getAllGenres(): Result<List<Genre>> {
        return try {
            val response = genreApi.getAllGenres()
            Result.success(response.map { CatalogMapper.genreToDomain(it) })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading genres", e)
            Result.failure(e)
        }
    }
    
    override suspend fun getAllAuthors(): Result<List<Author>> {
        return try {
            val response = authorApi.getAllAuthors(size = 1000)
            Result.success(response.content.map { CatalogMapper.authorToDomain(it) })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading authors", e)
            Result.failure(e)
        }
    }
    
    override fun getBookCoverUrl(bookId: Int): String {
        return "${NetworkModule.BASE_URL}api/books/$bookId/cover"
    }
}
