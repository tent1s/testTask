package com.example.testapplt.domain.model

import android.net.Uri

data class BooksInfo(
    val id: String,
    val tittle: String?,
    val authors: String?,
    val image: Uri?
)