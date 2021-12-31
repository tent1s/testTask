package com.example.testapplt.data.remote.repository.book

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.repository.GoogleBookRepository
import javax.inject.Inject

class GoogleBookRepositoryImp @Inject constructor(
    private val googleBookApi: GoogleBookApi
    ): GoogleBookRepository {

    override suspend fun getBooks() : Either<ErrorReason, Unit> =
        googleBookApi.getBooks()


}