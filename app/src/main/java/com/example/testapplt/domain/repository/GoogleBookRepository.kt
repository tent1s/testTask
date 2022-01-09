package com.example.testapplt.domain.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo
import kotlinx.coroutines.flow.Flow

interface GoogleBookRepository {
    suspend fun getPagingBooks(query: String): Flow<PagingData<BooksInfo>>
}