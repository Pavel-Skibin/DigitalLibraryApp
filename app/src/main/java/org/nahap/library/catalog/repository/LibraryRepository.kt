package org.nahap. library.catalog.repository

import android.util.Log
import org.nahap.library.catalog. api.AuthorApi
import org.nahap.library.catalog. api.GenreApi
import org.nahap.library.catalog. api.LibraryApi
import org.nahap.library.catalog.model.AuthorResponse
import org.nahap.library.catalog.model.BookDetailResponse
import org.nahap. library.catalog.model.BookResponse
import org.nahap.library.catalog.model.GenreResponse
import org.nahap.library.catalog.model.PageResponse
import org.nahap.library.catalog.model.SearchFilters
import org.nahap.library.di.NetworkModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val libraryApi: LibraryApi,
    private val genreApi: GenreApi,
    private val authorApi: AuthorApi
) {
    companion object {
        private const val TAG = "LibraryRepository"
        private const val PAGE_SIZE = 10

        const val GENRE_PHILOSOPHY = 7
        const val GENRE_DRAMA = 2
        const val GENRE_CLASSICS = 6
    }

    suspend fun getBooksByGenre(genreId: Int, page: Int = 0): Result<PageResponse<BookResponse>> {
        return try {
            Log.d(TAG, "Loading books for genre $genreId, page $page")
            val response = libraryApi.searchBooks(
                title = "",
                genreIds = listOf(genreId),
                page = page,
                size = PAGE_SIZE,
                sort = "title,asc"
            )
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading books for genre $genreId", e)
            Result.failure(e)
        }
    }

    suspend fun getTopRatedBooks(page: Int = 0): Result<List<BookResponse>> {
        return try {
            Log.d(TAG, "Loading top rated books, page $page")
            val response = libraryApi.getTopRatedBooks(
                minRatings = 3,
                page = page,
                size = PAGE_SIZE
            )
            val books = response.content. map { stat ->
                BookResponse(
                    id = stat.bookId,
                    title = stat.title,
                    description = null,
                    authors = null,
                    genres = null,
                    averageRating = stat.averageRating,
                    totalRatings = stat.ratingCount,
                    coverUrl = null
                )
            }
            Result.success(books)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading top rated books", e)
            Result.failure(e)
        }
    }

    suspend fun getBookDetails(bookId: Int): Result<BookDetailResponse> {
        return try {
            Log.d(TAG, "Loading book details for ID $bookId")
            val response = libraryApi.getBookDetails(bookId)
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading book details for ID $bookId", e)
            Result.failure(e)
        }
    }

    suspend fun searchBooks(query: String, page: Int = 0): Result<PageResponse<BookResponse>> {
        return try {
            Log.d(TAG, "Searching books: '$query', page $page")
            val response = libraryApi.searchBooks(
                title = query,
                page = page,
                size = PAGE_SIZE,
                sort = "title,asc"
            )
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching books", e)
            Result.failure(e)
        }
    }

    suspend fun searchBooksWithFilters(
        filters: SearchFilters,
        page: Int = 0
    ): Result<PageResponse<BookResponse>> {
        return try {
            Log.d(TAG, "Searching with filters, page $page")
            val response = libraryApi.searchBooks(
                title = filters.title. ifEmpty { "" },
                authorIds = filters.selectedAuthors. ifEmpty { null },
                genreIds = filters.selectedGenres.ifEmpty { null },
                minRating = filters.minRating,
                maxRating = filters. maxRating,
                page = page,
                size = PAGE_SIZE,
                sort = filters.sortBy.value
            )
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching with filters", e)
            Result. failure(e)
        }
    }

    suspend fun getAllGenres(): Result<List<GenreResponse>> {
        return try {
            val response = genreApi.getAllGenres()
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading genres", e)
            Result.failure(e)
        }
    }

    suspend fun getAllAuthors(): Result<List<AuthorResponse>> {
        return try {
            val response = authorApi.getAllAuthors(size = 1000)
            Result.success(response. content)
        } catch (e: Exception) {
            Log. e(TAG, "Error loading authors", e)
            Result.failure(e)
        }
    }

    fun getBookCoverUrl(bookId: Int): String {
        return "${NetworkModule.BASE_URL}api/books/$bookId/cover"
    }
}