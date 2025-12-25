package org.nahap.library.reader.api

import retrofit2.http.GET
import retrofit2.http.Path

interface BookApi {


    @GET("api/books/{bookId}/fb2")
    suspend fun getBookFb2(@Path("bookId") bookId: Int): String
}