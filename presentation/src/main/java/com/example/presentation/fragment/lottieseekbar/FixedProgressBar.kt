package com.example.presentation.fragment.lottieseekbar

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar

class FixedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isSetPadding) {
            val aa = progressDrawable
            progressDrawable = null
            progressDrawable = aa
            isSetPadding = false
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private var isSetPadding = false
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        isSetPadding = true
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        isSetPadding = true
    }
}