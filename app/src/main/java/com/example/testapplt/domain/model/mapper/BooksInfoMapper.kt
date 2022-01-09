package com.example.testapplt.domain.model.mapper

import android.net.Uri
import android.text.TextUtils.join
import com.example.testapplt.domain.model.BooksInfo
import com.example.testapplt.data.remote.book.entity.ItemsInfo

object BooksInfoMapper {
    fun map(data: ItemsInfo): BooksInfo =
        BooksInfo(
            id = data.id,
            tittle = data.volumeInfo.title,
            authors = data.volumeInfo.authors?.let { join(",", it) },
            image = data.volumeInfo.imageLinks?.let { Uri.parse(it.thumbnail) }
        )
}