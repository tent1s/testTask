package com.example.testapplt.domain.repository

import androidx.paging.Pager
import com.example.testapplt.domain.model.Either
import com.example.testapplt.domain.model.ErrorReason
import com.example.testapplt.domain.model.domain.BooksInfo

interface GoogleBookRepository {
    suspend fun getBooksFlow(searchParam: String, filter: String): Pager<Int, BooksInfo>
}