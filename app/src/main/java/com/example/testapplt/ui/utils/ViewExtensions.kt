package com.example.testapplt.ui.utils

import android.view.View

fun View.hide(collapse: Boolean = true) {
    visibility = if (collapse) View.GONE else View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}