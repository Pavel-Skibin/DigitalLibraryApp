package org.nahap.library.reader.api

import org.nahap.library.reader.model.BookmarkCreateRequest
import org.nahap.library.reader.model.BookmarkUpdateRequest
import org.nahap.library.reader.model.BookmarkResponse
import retrofit2.http.*

interface BookmarkApi {

    @GET("api/bookmarks/book/{bookId}")
    suspend fun getBookmarks(
        @Path("bookId") bookId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): BookmarksPageResponse

    @POST("api/bookmarks")
    suspend fun createBookmark(
        @Body request: BookmarkCreateRequest
    ): BookmarkResponse

    @PUT("api/bookmarks/{bookmarkId}")
    suspend fun updateBookmark(
        @Path("bookmarkId") bookmarkId: Int,
        @Body request: BookmarkUpdateRequest
    ): BookmarkResponse

    @DELETE("api/bookmarks/{bookmarkId}")
    suspend fun deleteBookmark(
        @Path("bookmarkId") bookmarkId: Int
    )
}

data class BookmarksPageResponse(
    val content: List<BookmarkResponse>,
    val totalPages: Int,
    val totalElements: Int
)