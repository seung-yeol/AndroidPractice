package com.example.presentation.custom.drawable

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


class ShadowDrawable2(context: Context) : Drawable(), Animatable {
    companion object {
        private const val FRAME_DELAY = 1000 / 50
    }

    private val drawable = ContextCompat.getDrawable(context, R.drawable.shadow_diamond)
    private var isRunning = false
    private var startTime = 0L

    private var curCount = 0
    private var repeatCount = 2

    private var duration = 1400L
    private var repeatDuration = 700L

    private val startEnd = 200L //shadow 크기가 변하는 시간.

    override fun draw(canvas: Canvas) {
        drawDiamondBlur(canvas)
    }

    private fun drawDiamondBlur(canvas: Canvas) {
        if (isRunning) {
            val elapsedTime = SystemClock.uptimeMillis() - startTime

            var width = 1
            var height = 1
            when {
                elapsedTime <= startEnd -> {
                    val per = elapsedTime / startEnd.toFloat()
                    width = (drawable!!.intrinsicWidth * per).toInt()
                    height = (drawable.intrinsicHeight * per).toInt()
                }
                elapsedTime >= duration - startEnd -> {
                    val per = (duration - elapsedTime) / startEnd.toFloat()
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

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun isRunning(): Boolean {
        return isRunning
    }

    override fun start() {
        curCount = 0
        isRunning = true
        startTime = SystemClock.uptimeMillis()
        scheduleSelf(mUpdater, SystemClock.uptimeMillis())
        invalidateSelf()
    }

    override fun stop() {
        curCount = 0
        isRunning = false
        unscheduleSelf(mUpdater)
        invalidateSelf()
    }

    private fun restart() {
        curCount++
        startTime = SystemClock.uptimeMillis() + repeatDuration
        scheduleSelf(mUpdater, startTime)
        invalidateSelf()
    }

    private val mUpdater = Runnable { update() }
    private fun update() {
        val curTime = SystemClock.uptimeMillis()
        val progress = min(1f, (curTime - startTime).toFloat() / duration)

        if (repeatCount == curCount) {
            isRunning = false
            curCount = 0
        } else {
            if (progress == 1f) {
                restart()
            }
        }

        if (isRunning) {
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DELAY)
        }
        invalidateSelf()
    }

    override fun jumpToCurrentState() {
        super.jumpToCurrentState()
        stop()
    }
}