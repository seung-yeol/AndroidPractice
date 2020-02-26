package com.example.presentation.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.presentation.R
import kotlin.math.max

class IconSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.seekBarStyle,
    defStyleRes: Int = 0
) : SeekBar(context, attrs, defStyleAttr, defStyleRes) {

    init {
        initAttrs(attrs)
    }

    private lateinit var iconBitmap: Bitmap
    private val iconRect = Rect()
    private val iconWidth get() = iconBitmap.width
    private val iconHeight get() = iconBitmap.height

    private var originPaddingStart: Int = 0
    private var originPaddingEnd: Int = 0
    private var originPaddingTop: Int = 0
    private var originPaddingBottom: Int = 0

    private fun initAttrs(attrs: AttributeSet?) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.IconSeekBar)
        val iconDrawable = typeArray.getDrawable(R.styleable.IconSeekBar_iconDrawable)
        val iconOffset = typeArray.getDimensionPixelSize(R.styleable.IconSeekBar_iconOffset, 0)
        typeArray.recycle()

        originPaddingStart = paddingStart
        originPaddingEnd = paddingEnd
        originPaddingTop = paddingTop
        originPaddingBottom = paddingBottom

        initIconBitmap(iconDrawable)
        initPadding(iconOffset)
    }

    private fun initIconBitmap(iconDrawable: Drawable?) {
        iconBitmap = iconDrawable?.toBitmap()
            ?: Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888).also {
                Canvas(it).drawCircle(
                    25f,
                    25f,
                    it.width.toFloat() / 2,
                    Paint().apply { color = Color.RED })
            }
    }

    private fun initPadding(iconOffset: Int) {
        val realPaddingStart = max(originPaddingStart, iconWidth / 2)
        val realPaddingEnd = max(originPaddingEnd, iconWidth / 2)
        val realPaddingTop = originPaddingTop + iconHeight - iconOffset
        val realPaddingBottom = originPaddingBottom + iconOffset
        setPadding(realPaddingStart, realPaddingTop, realPaddingEnd, realPaddingBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawIcon(canvas)
    }

    private fun drawIcon(canvas: Canvas?) {
        val padding: Int = paddingLeft + paddingRight
        val sPos: Int = left + paddingLeft
        val xPos: Int = (width - padding) * progress / max + sPos - iconWidth / 2

        iconRect.left = xPos
        iconRect.top = height - paddingBottom

        canvas?.drawBitmap(iconBitmap, iconRect.left.toFloat(), 0f, null)
    }

    fun getProgressAlignY(): Int {
        return progressDrawable.bounds.run {
            height() / 2 + paddingBottom
        }
    }

    fun setIcon(@DrawableRes drawableId: Int, iconOffset: Int) {
        val iconDrawable = ContextCompat.getDrawable(context, drawableId)
        initIconBitmap(iconDrawable)
        initPadding(iconOffset)
    }

    fun getProgressX(): Int {
        return iconRect.left + iconWidth / 2
    }
}
