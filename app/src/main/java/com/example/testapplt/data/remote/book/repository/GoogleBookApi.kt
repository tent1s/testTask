package com.example.testapplt.data.remote.book.repository

import com.example.testapplt.utils.ErrorReason
import com.example.testapplt.data.remote.book.entity.BooksInfoNetwork
import com.example.testapplt.utils.Either
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBookApi {

    @GET("v1/volumes")
    suspend fun getBooks(
        @Query("q") parameter: String,
        @Query("maxResults") maxResults: Int,
        @Query("startIndex") startIndex: Int,
    ): Either<ErrorReason, BooksInfoNetwork>

}