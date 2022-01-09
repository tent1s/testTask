package com.example.testapplt.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testapplt.LOADING_GOOGLE_BOOKS_PAGE_SIZE
import com.example.testapplt.data.remote.book.repository.GoogleBookApi
import com.example.testapplt.domain.model.BooksInfo
import com.example.testapplt.domain.model.mapper.BooksInfoMapper
import com.example.testapplt.utils.Either

class BooksPagingSource(
    private val googleBookApi: GoogleBookApi,
    private val query: String
) : PagingSource<Int, BooksInfo>() {

    companion object {
        private const val BOOKS_STARTING_PAGE_INDEX = 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BooksInfo> {
        val page = params.key ?: BOOKS_STARTING_PAGE_INDEX
        return when (val response =
            googleBookApi.getBooks(query, LOADING_GOOGLE_BOOKS_PAGE_SIZE, LOADING_GOOGLE_BOOKS_PAGE_SIZE * page)) {
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