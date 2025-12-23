package org.nahap.library.catalog.api

import org.nahap.library.catalog.model.GenreResponse
import retrofit2.http.*

interface GenreApi {

    @GET("api/genres")
    suspend fun getAllGenres(): List<GenreResponse>

    @GET("api/genres/{id}")
    suspend fun getGenreById(@Path("id") genreId: Int): GenreResponse
}