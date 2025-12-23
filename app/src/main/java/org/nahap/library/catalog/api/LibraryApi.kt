package org.nahap.library.catalog.api

import org.nahap.library. catalog.model.BookDetailResponse
import org.nahap. library.catalog.model.BookResponse
import org.nahap.library.catalog.model.BookStatResponse
import org.nahap.library.catalog.model.PageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LibraryApi {

    @GET("api/books")
    suspend fun getAllBooks(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "title,asc"
    ): PageResponse<BookResponse>

    @GET("api/books/{id}")
    suspend fun getBookDetails(@Path("id") bookId: Int): BookDetailResponse

    @GET("api/books/search")
    suspend fun searchBooks(
        @Query("title") title: String? = "",
        @Query("authorIds") authorIds: List<Int>? = null,
        @Query("genreIds") genreIds: List<Int>? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("maxRating") maxRating: Double? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "title,asc"
    ): PageResponse<BookResponse>

    @GET("api/statistics/books/top-rated")
    suspend fun getTopRatedBooks(
        @Query("minRatings") minRatings: Long = 3,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PageResponse<BookStatResponse>


}