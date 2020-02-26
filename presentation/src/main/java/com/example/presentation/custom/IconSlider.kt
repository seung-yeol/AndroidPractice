package com.example.presentation.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.presentation.R

class IconSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.seekBarStyle,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var seekBar: SeekBar
    private var iconBitmap: Bitmap? = null
    private val iconRect = Rect()

    init {
        initSeekBar(context, attrs, defStyleAttr, defStyleRes)
        initAttrs(attrs)
    }

    private fun initSeekBar(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        seekBar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            SeekBar(context, attrs, defStyleAttr, defStyleRes)
        else SeekBar(context, attrs, defStyleAttr)
    }

    private fun initAttrs(attrs: AttributeSet?) {
//        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.IconSlider)
//        val iconDrawable = typeArray.getDrawable(R.styleable.IconSlider_iconDrawable)

//        typeArray.recycle()
//
//        iconBitmap = iconDrawable?.toBitmap()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getSize(heightMeasureSpec)

        heightSize = if (heightMode == MeasureSpec.EXACTLY) {
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        } else if (
            heightMode == MeasureSpec.AT_MOST &&
            parent is ViewGroup &&
            heightSize == ViewGroup.LayoutParams.MATCH_PARENT
        ) {
            MeasureSpec.makeMeasureSpec((parent as ViewGroup).measuredHeight, MeasureSpec.AT_MOST)
        } else {
            val heightNeeded: Int = seekBar.paddingTop + seekBar.top
            MeasureSpec.makeMeasureSpec(heightNeeded, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        seekBar.draw(canvas)
        drawIcon(canvas)
    }

    private fun drawIcon(canvas: Canvas?) {
        val padding: Int = seekBar.paddingLeft + seekBar.paddingRight
        val sPos: Int = seekBar.left + seekBar.paddingLeft
        val xPos: Int =
            (seekBar.width - padding) * seekBar.progress / seekBar.max + sPos - iconBitmap!!.width / 2

        val top = seekBar.paddingTop

        iconRect.left = xPos
        iconRect.top =
            canvas!!.height - iconBitmap!!.height - seekBar.paddingBottom - iconBitmap!!.height / 2

        val top1 = canvas.height - iconBitmap!!.height - seekBar.paddingBottom
        val top2 = seekBar.thumb.bounds.height()

        canvas.drawBitmap(iconBitmap!!, iconRect.left.toFloat(), iconRect.top.toFloat(), null)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        seekBar.setPadding(left, top, right, bottom)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        invalidate()
        return super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width: Int = seekBar.measuredWidth
        val height: Int = seekBar.measuredHeight
    }
}
