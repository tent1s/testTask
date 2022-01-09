package com.example.testapplt.ui.screen.books

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize



@Parcelize
@Keep
enum class SearchType(val typeSignature: String) : Parcelable {
    ALL(""),
    AUTHOR("inauthor:"),
    NAME_VOLUME("intitle:"),
    SUBJECT("subject:"),
    PUBLISHER("inpublisher:"),
}