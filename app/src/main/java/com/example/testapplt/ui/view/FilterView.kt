package com.example.testapplt.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.testapplt.R
import com.example.testapplt.ui.utils.hide
import com.example.testapplt.ui.utils.show
import kotlinx.android.synthetic.main.view_filter.view.*

class FilterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var isActive: Boolean = false
        set(value) {
            field = value
            if (value) showChoseState() else showBaseState()
        }

    private var title: String? = null

    init {
        inflate(context, R.layout.view_filter, this)
        with(context.obtainStyledAttributes(attrs, R.styleable.FilterView)) {
            title = getString(R.styleable.FilterView_text).orEmpty()
            recycle()
        }
        showBaseState()
        filterViewTextView.text = title.orEmpty()
    }


    private fun showChoseState() {
        filterViewCheckImageView.show()
    }

    private fun showBaseState() {
        filterViewCheckImageView.hide()
    }

}