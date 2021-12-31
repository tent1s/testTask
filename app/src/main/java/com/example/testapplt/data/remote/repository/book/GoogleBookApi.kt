package com.example.testapplt.data.remote.repository.book

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.network.BooksInfoNetwork
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBookApi {

    @GET("v1/volumes")
    suspend fun getBooks(
        @Query("q") parameter : String
    ): Either<ErrorReason, BooksInfoNetwork>

}