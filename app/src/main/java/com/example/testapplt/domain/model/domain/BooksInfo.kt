package com.example.testapplt.domain.model.domain

import android.net.Uri

data class BooksInfo (
    val tittle : String,
    val authors : List<String>?,
    val image: Uri?)