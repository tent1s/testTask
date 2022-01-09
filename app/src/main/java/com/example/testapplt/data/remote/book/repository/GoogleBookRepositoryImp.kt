package com.example.testapplt.data.remote.book.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.testapplt.LOADING_GOOGLE_BOOKS_PAGE_SIZE
import com.example.testapplt.data.paging.BooksPagingSource
import com.example.testapplt.domain.repository.GoogleBookRepository
import javax.inject.Inject

class GoogleBookRepositoryImp @Inject constructor(
    private val googleBookApi: GoogleBookApi
) : GoogleBookRepository {

    override suspend fun getPagingBooks(
        query: String
    ) = Pager(
        config = PagingConfig(
            pageSize = LOADING_GOOGLE_BOOKS_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { BooksPagingSource(googleBookApi, query) }
    ).flow

}