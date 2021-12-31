package com.example.testapplt.ui.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.testapplt.R


fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.hide(collapse: Boolean = true) {
    visibility = if (collapse) View.GONE else View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun ImageView.load(url: Uri){
        Glide
            .with(this.context)
            .load(url)
            .placeholder(R.drawable.placeholder_book)
            .dontAnimate()
            .into(this)
}

fun ImageView.load(drawable: Drawable){
        Glide
            .with(this.context)
            .load(drawable)
            .into(this)
}