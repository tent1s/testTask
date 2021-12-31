package com.example.testapplt.data.remote.repository.book

import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.model.domain.mapper.BooksInfoMapper
import com.example.testapplt.domain.model.map
import com.example.testapplt.domain.repository.GoogleBookRepository
import javax.inject.Inject

class GoogleBookRepositoryImp @Inject constructor(
    private val googleBookApi: GoogleBookApi
    ): GoogleBookRepository {

    override suspend fun getBooks(parameter: String) : Either<ErrorReason, List<BooksInfo>?> =
        googleBookApi.getBooks(parameter).map {
            it.items?.map(BooksInfoMapper::map)
        }

}