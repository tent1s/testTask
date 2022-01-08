package com.example.testapplt.domain.usecases

import androidx.paging.PagingData
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.repository.GoogleBookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListOfBooksUseCase @Inject constructor(
    private val googleBookRepository: GoogleBookRepository
) {
    suspend fun getBooksFlow(searchParam: String, filter: String): Flow<PagingData<BooksInfo>> =
        googleBookRepository.getBooksFlow(searchParam, filter).flow
}