package com.example.presentation.fragment.drawable.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.example.presentation.R


class MyDrawable(context: Context) : Drawable(), Animatable {
    private val drawable = ContextCompat.getDrawable(context, R.drawable.small_shadow_diamond)
    private val frameDelay = 1000 / 50        // 초당 50프레임
    private var duration = 1400L            // 애니메이션의 총 시간
    private var isRunning = false            // 애니메이션 실행상태
    private var startTime = 0L                // 시작시간

    override fun draw(canvas: Canvas) {
        if (isRunning) {
            val elapsedTime = SystemClock.uptimeMillis() - startTime

            val per = elapsedTime / duration
            val width = (drawable!!.intrinsicWidth * per).toInt()
            val height = (drawable.intrinsicHeight * per).toInt()

            drawable.setBounds(
                (drawable.intrinsicWidth - width) / 2,
                (drawable.intrinsicHeight - height) / 2,
                (drawable.intrinsicWidth - width) / 2 + width,
                (drawable.intrinsicHeight - height) / 2 + height
            )

            drawable.draw(canvas)
        }
    }

    override fun getIntrinsicWidth(): Int {
        return drawable?.intrinsicWidth ?: -1
    }

    override fun getIntrinsicHeight(): Int {
        return drawable?.intrinsicHeight ?: -1
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun isRunning(): Boolean {
        return isRunning
    }

    override fun start() {
        isRunning = true
        startTime = SystemClock.uptimeMillis()
        scheduleSelf(mUpdater, SystemClock.uptimeMillis())
        invalidateSelf()
    }

    override fun stop() {
        isRunning = false
        unscheduleSelf(mUpdater)
        invalidateSelf()
    }


    private val mUpdater = Runnable { update() }
    private fun update() {
        val curTime = SystemClock.uptimeMillis()

        if (curTime - startTime > duration) {
            isRunning = false
        }

        if (isRunning) {
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + frameDelay)
        }
        invalidateSelf()
    }

    override fun jumpToCurrentState() {
        super.jumpToCurrentState()
        stop()
    }
}
