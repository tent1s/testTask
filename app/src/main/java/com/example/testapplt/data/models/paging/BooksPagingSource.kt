package com.example.testapplt.data.models.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testapplt.data.remote.repository.book.GoogleBookApi
import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.model.domain.mapper.BooksInfoMapper

class BooksPagingSource(
    private val googleBookApi: GoogleBookApi,
    private val query: String
) : PagingSource<Int, BooksInfo>() {

    companion object {
        private const val BOOKS_STARTING_PAGE_INDEX = 0
        private const val LOAD_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BooksInfo> {
        val page = params.key ?: BOOKS_STARTING_PAGE_INDEX
        return when (val response =
            googleBookApi.getBooks(query, LOAD_SIZE, LOAD_SIZE * page)) {
            is Either.Failure -> LoadResult.Error(RuntimeException(response.error.message))
            is Either.Success -> {
                val books = response.data.items?.map(BooksInfoMapper::map) ?: run { listOf() }
                LoadResult.Page(
                    data = books,
                    prevKey = if (page == BOOKS_STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (books.isEmpty()) null else page + 1
                )
            }
        }

    }

    override fun getRefreshKey(state: PagingState<Int, BooksInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}