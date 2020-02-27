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
import kotlin.math.min


class ShadowDrawable(context: Context) : Drawable(), Animatable {
    companion object {
        private const val DURATION = 1400L
        private const val FRAME_DELAY = 1000 / 50
        private const val REPEAT_DURATION = 700L
        private const val REPEAT_COUNT = 2
        private const val RESIZE_START_END = 200L //shadow 크기가 변하는 시간.
    }

    private val drawable = ContextCompat.getDrawable(context, R.drawable.diaomd_blur)
    private var isRunning = false
    private var startTime = 0L
    private var curRepeatCount = 0

    override fun draw(canvas: Canvas) {
        drawDiamondBlur(canvas)
    }

    private fun drawDiamondBlur(canvas: Canvas) {
        if (isRunning) {
            val elapsedTime = SystemClock.uptimeMillis() - startTime

            var width: Int
            var height: Int
            when {
                elapsedTime <= RESIZE_START_END -> {
                    val per = elapsedTime / RESIZE_START_END.toFloat()
                    width = (drawable!!.intrinsicWidth * per).toInt()
                    height = (drawable.intrinsicHeight * per).toInt()
                }
                elapsedTime >= DURATION - RESIZE_START_END -> {
                    val per = (DURATION - elapsedTime) / RESIZE_START_END.toFloat()
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

    override fun jumpToCurrentState() {
        super.jumpToCurrentState()
        stop()
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun isRunning(): Boolean {
        return isRunning
    }

    override fun start() {
        curRepeatCount = 0
        isRunning = true
        startTime = SystemClock.uptimeMillis()
        scheduleSelf(mUpdater, SystemClock.uptimeMillis())
        invalidateSelf()
    }

    override fun stop() {
        curRepeatCount = 0
        isRunning = false
        unscheduleSelf(mUpdater)
        invalidateSelf()
    }

    private fun restart() {
        curRepeatCount++
        startTime = SystemClock.uptimeMillis() + REPEAT_DURATION
        scheduleSelf(mUpdater, startTime)
        invalidateSelf()
    }

    private val mUpdater = Runnable { update() }
    private fun update() {
        val curTime = SystemClock.uptimeMillis()
        val progress = min(1f, (curTime - startTime).toFloat() / DURATION)
        val drawPause =
            min(curTime - startTime, 200L) == 200L && min(curTime - startTime, 1000L) != 1000L

        if (REPEAT_COUNT == curRepeatCount) {
            isRunning = false
            curRepeatCount = 0
        } else {
            if (progress == 1f) {
                restart()
            }
        }

        if (isRunning) {
            if (!drawPause)
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DELAY)
            else
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + DURATION - RESIZE_START_END * 2)
        }
        invalidateSelf()
    }
}