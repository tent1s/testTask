package com.example.testapplt.domain.repository

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason

interface GoogleBookRepository {
    suspend fun getBooks(): Either<ErrorReason, Unit>
}