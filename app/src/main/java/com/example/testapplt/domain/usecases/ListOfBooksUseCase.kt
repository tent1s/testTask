package com.example.testapplt.domain.usecases

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.repository.GoogleBookRepository
import javax.inject.Inject

class ListOfBooksUseCase @Inject constructor(
    private val googleBookRepository: GoogleBookRepository)
{

    suspend fun getBooks(searchParam: String, parameter: String, startIndex: Int = 0): Either<ErrorReason, List<BooksInfo>?> =
        googleBookRepository.getBooks(searchParam, parameter, startIndex)
}