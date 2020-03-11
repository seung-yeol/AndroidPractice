package com.example.presentation.fragment.drawable.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.presentation.R

class ShadowDrawable2(context: Context) : Drawable() {
    private val drawable = ContextCompat.getDrawable(context, R.drawable.diaomd_blur)
    private val startRatio = 0.18f //shadow 크기가 변하는 시간.
    private val endRatio = 0.4f //shadow 크기가 변하는 시간.

    fun setProgress(progress : Float) {
        var width = 1
        var height = 1
        when {
            progress <= startRatio -> {
                val per = progress / startRatio
                width = (drawable!!.intrinsicWidth * per).toInt()
                height = (drawable.intrinsicHeight * per).toInt()
            }
            progress >= 1 - endRatio -> {
                val per = (1f - progress) / endRatio
                width = (drawable!!.intrinsicWidth * per).toInt()
                height = (drawable.intrinsicHeight * per).toInt()
            }
            else -> {
                width = drawable!!.intrinsicWidth
                height = drawable.intrinsicHeight
            }
        }

        if (width == 0) width = 1
        if (height == 0) height = 1

        drawable.setBounds(
            (drawable.intrinsicWidth - width) / 2,
            (drawable.intrinsicHeight - height) / 2,
            (drawable.intrinsicWidth - width) / 2 + width,
            (drawable.intrinsicHeight - height) / 2 + height
        )

        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        drawable?.draw(canvas)
    }

    override fun getIntrinsicWidth(): Int {
        return drawable?.intrinsicWidth ?: -1
    }

    override fun getIntrinsicHeight(): Int {
        return drawable?.intrinsicHeight ?: -1
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}