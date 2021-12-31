package com.example.testapplt.domain.usecases

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.repository.GoogleBookRepository
import javax.inject.Inject

class ListOfBooksUseCase @Inject constructor(
    private val googleBookRepository: GoogleBookRepository)
{

    suspend fun getBooks(parameter: String): Either<ErrorReason, List<BooksInfo>?> =
        googleBookRepository.getBooks(parameter)
}