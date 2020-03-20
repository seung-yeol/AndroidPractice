package com.example.presentation.fragment.customlayout

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

class MyTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {


    init {
        setOnTouchListener { _, _ ->
            println("osy view onTouchListener")
            false
        }

        setOnClickListener {
            println("osy view onClickListener")
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        println("osy view dispatchTouchEvent")
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("osy view onTouchEvent")
        return super.onTouchEvent(event)
    }
}