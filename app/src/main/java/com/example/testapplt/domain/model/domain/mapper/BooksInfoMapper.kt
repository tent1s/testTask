package com.example.testapplt.domain.model.domain.mapper

import android.net.Uri
import com.example.testapplt.domain.model.domain.BooksInfo
import com.example.testapplt.domain.model.network.BooksInfoNetwork
import com.example.testapplt.domain.model.network.ItemsInfo

object BooksInfoMapper {
    fun map(data: ItemsInfo) : BooksInfo =
        BooksInfo(
            tittle = data.volumeInfo.title,
            authors = data.volumeInfo.authors,
            image = data.volumeInfo.imageLinks?.let { Uri.parse(it.thumbnail) }
        )
}