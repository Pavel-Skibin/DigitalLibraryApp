package org. nahap.library.catalog.api

import org.nahap.library.catalog.model.PagedAuthorsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthorApi {

    @GET("api/authors")
    suspend fun getAllAuthors(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 1000
    ): PagedAuthorsResponse
}