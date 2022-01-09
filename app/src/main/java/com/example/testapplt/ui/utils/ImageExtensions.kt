package com.example.testapplt.ui.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.testapplt.R

fun ImageView.load(url: Uri) {
    Glide
        .with(this.context)
        .load(url)
        .placeholder(R.drawable.placeholder_book)
        .dontAnimate()
        .into(this)
}

fun ImageView.load(drawable: Drawable) {
    Glide
        .with(this.context)
        .load(drawable)
        .into(this)
}