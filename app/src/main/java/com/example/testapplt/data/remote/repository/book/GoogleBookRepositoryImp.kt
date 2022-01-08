package com.example.testapplt.data.remote.repository.book

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.testapplt.data.models.paging.BooksPagingSource
import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.model.domain.mapper.BooksInfoMapper
import com.example.testapplt.domain.model.map
import com.example.testapplt.domain.repository.GoogleBookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GoogleBookRepositoryImp @Inject constructor(
    private val googleBookApi: GoogleBookApi
) : GoogleBookRepository {

    override suspend fun getBooksFlow(
        searchParam: String,
        filter: String
    ) = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { BooksPagingSource(googleBookApi, "$filter$searchParam") }
    )

}