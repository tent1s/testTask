package com.example.testapplt.data.remote.repository.book

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import retrofit2.http.GET

interface GoogleBookApi {

    @GET("v1/volumes")
    suspend fun getBooks(): Either<ErrorReason, Unit>

}