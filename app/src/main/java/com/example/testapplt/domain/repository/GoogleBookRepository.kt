package com.example.testapplt.domain.repository

import androidx.paging.PagingData
import com.example.testapplt.domain.model.BooksInfo
import kotlinx.coroutines.flow.Flow

interface GoogleBookRepository {
    suspend fun getPagingBooks(query: String): Flow<PagingData<BooksInfo>>
}