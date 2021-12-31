package com.example.testapplt.domain.model.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BooksInfoNetwork (
    @Json(name = "items")
    val items: List<ItemsInfo>?,
)

@JsonClass(generateAdapter = true)
class ItemsInfo(
    @Json(name = "volumeInfo")
    val volumeInfo : VolumeInfo,
)

@JsonClass(generateAdapter = true)
class VolumeInfo(
    @Json(name = "title")
    val title : String,
    @Json(name = "authors")
    val authors : List<String>?,
    @Json(name = "imageLinks")
    val imageLinks : ImageLinks?
)

@JsonClass(generateAdapter = true)
class ImageLinks(
    @Json(name = "thumbnail")
    val thumbnail : String
)