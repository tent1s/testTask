package com.example.testapplt.data.remote.repository.book

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.testapplt.data.models.paging.BooksPagingSource
import com.example.testapplt.domain.repository.GoogleBookRepository
import javax.inject.Inject

class GoogleBookRepositoryImp @Inject constructor(
    private val googleBookApi: GoogleBookApi
) : GoogleBookRepository {

    override suspend fun getPagingBooks(
        query: String
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { BooksPagingSource(googleBookApi, query) }
    ).flow

}