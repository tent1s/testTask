package com.example.testapplt.domain.repository

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo

interface GoogleBookRepository {
    suspend fun getBooks(parameter: String): Either<ErrorReason, List<BooksInfo>?>
}