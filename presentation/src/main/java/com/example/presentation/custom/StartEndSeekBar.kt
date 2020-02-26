package com.example.presentation.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.annotation.DrawableRes
import com.example.presentation.R
import com.osy.util.dpToPx
import kotlinx.android.synthetic.main.highlight_view.view.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class StartEndSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
        initAttrs(context, attrs, defStyleAttr)
        initListener()
        setWillNotDraw(false)
    }

    private var startIcon: Drawable? = null
    private var endIcon: Drawable? = null
    private var startProgress: Int = 0
    private var endProgress: Int = 0

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        View.inflate(context, R.layout.highlight_view, this)
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.StartEndSeekBar, defStyleAttr, 0)

        startProgress = typedArray.getInt(R.styleable.StartEndSeekBar_leftProgress, 0)
        endProgress = typedArray.getInt(R.styleable.StartEndSeekBar_rightProgress, 0)
        startIcon = typedArray.getDrawable(R.styleable.StartEndSeekBar_startIcon)
        endIcon = typedArray.getDrawable(R.styleable.StartEndSeekBar_endIcon)
        typedArray.recycle()
    }

    private fun initListener() {
        leftSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekBar?.secondaryProgress = min(progress, mainSeekBar.progress)
                invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mainSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                leftSeekBar?.secondaryProgress = min(progress, leftSeekBar.progress)
                backSeekBar.progress = progress
                invalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        drawRange(canvas)
        drawMark(canvas)
    }

    private fun drawRange(canvas: Canvas?) {
        val width = abs(rightSeekBar.getProgressX() - leftSeekBar.getProgressX())
        val left = leftSeekBar.getProgressX()
        val right = rightSeekBar.getProgressX()

        if (width != 0) {
            val bitmap = Bitmap.createBitmap(width, 5.dpToPx(), Bitmap.Config.ARGB_8888).also {
                Canvas(it).drawColor(Color.parseColor("#FFf35B"))
            }
            canvas!!.drawBitmap(
                bitmap,
                left.toFloat(),
                (canvas.height - 24 - 6.dpToPx()).toFloat(),
                null
            )
        }

//        canvas!!.save()
        /*canvas.translate(
            min(leftSeekBar.getProgressX(), rightSeekBar.getProgressX()).toFloat(),
            (canvas.height - 4.dpToPx()).toFloat() *//*0f*//*
        )*/

//        canvas.restore()
    }

    private fun drawMark(canvas: Canvas?) {
        val padding: Int = backSeekBar.paddingLeft + backSeekBar.paddingRight
        val sPos: Int = left + paddingLeft
        val xPos: Int = (width - padding) * backSeekBar.progress / backSeekBar.max + sPos

        val bitmap = Bitmap.createBitmap(width, 20.dpToPx(), Bitmap.Config.ARGB_8888).also {
            Canvas(it).drawCircle(
                20.dpToPx().toFloat() / 2,
                20.dpToPx().toFloat() / 2,
                10.dpToPx().toFloat(),
                Paint().apply { color = Color.WHITE })
        }

        canvas!!.drawBitmap(
            bitmap,
            xPos.toFloat() - 20.dpToPx() / 2 + backSeekBar.paddingStart,
            (canvas.height - 20.dpToPx() - backSeekBar.paddingBottom).toFloat(),
            null
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        val paddingStartEnd = max(startIcon?.intrinsicWidth ?: 0, endIcon?.intrinsicWidth ?: 0) / 2
        addPadding(paddingStartEnd.toFloat(), 0f)
    }

    private fun addPadding(paddingStartEnd: Float, paddingTop: Float) {
        val top = mainSeekBar.paddingTop + paddingTop
        val seekBarHeight = mainSeekBar.height - mainSeekBar.paddingTop - mainSeekBar.paddingBottom
        val paddingBottom = leftSeekBar.getProgressAlignY() - seekBarHeight / 2

        backSeekBar.setPadding(
            paddingStartEnd.toInt(),
            top.toInt(),
            paddingStartEnd.toInt(),
            paddingBottom
        )

        mainSeekBar.setPadding(
            paddingStartEnd.toInt(),
            top.toInt(),
            paddingStartEnd.toInt(),
            paddingBottom
        )
    }

    fun setLeftIcon(@DrawableRes drawableId: Int, iconOffset: Int) {
        leftSeekBar.setIcon(drawableId, iconOffset)
    }

    fun setRightIcon(@DrawableRes drawableId: Int, iconOffset: Int) {
        rightSeekBar.setIcon(drawableId, iconOffset)
    }
}