package com.example.presentation.fragment.rangeseekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewConfiguration
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.example.presentation.R
import com.osy.util.dpToPx
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class RangeSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr) {
    private var mScaledTouchSlop: Int = 0
    private var iconPaddingStartEnd: Int = 0
    private var iconPaddingTopBottom: Int = 0

    private var originPaddingStart: Int = 0
    private var originPaddingEnd: Int = 0
    private var originPaddingTop: Int = 0
    private var originPaddingBottom: Int = 0

    private var startProgress: Int = -1
    private var endProgress: Int = -1

    /*
      offset 값이 커질수록 아이콘이 아래 or 오른쪽으로 감.
      아이콘의 중간지점이 progress의 right | center_vertical 으로 되어 있음.
    */
    private var thumbOffsetStart: Int = 0
    private var startOffsetStart: Int = 0
    private var endOffsetStart: Int = 0
    private var thumbOffsetTop: Int = 0
    private var startOffsetTop: Int = 0
    private var endOffsetTop: Int = 0

    private var rangeColor: Int = Color.RED
    private var rangeHeight: Int = 4.dpToPx()

    private var isLastDrawStart: Boolean = true

    private lateinit var startIconDrawable: Drawable
    private lateinit var endIconDrawable: Drawable
    private lateinit var thumbIconDrawable: Drawable

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar).also {
            thumbOffsetStart = it.getDimensionPixelSize(R.styleable.RangeSeekBar_thumbIconOffsetStart, 0)
            startOffsetStart = it.getDimensionPixelSize(R.styleable.RangeSeekBar_startIconOffsetStart, 0)
            endOffsetStart = it.getDimensionPixelSize(R.styleable.RangeSeekBar_endIconOffsetStart, 0)

            thumbOffsetTop = it.getDimensionPixelSize(R.styleable.RangeSeekBar_thumbIconOffsetTop, 0)
            startOffsetTop = it.getDimensionPixelSize(R.styleable.RangeSeekBar_startIconOffsetTop, 0)
            endOffsetTop = it.getDimensionPixelSize(R.styleable.RangeSeekBar_endIconOffsetTop, 0)

            rangeHeight = it.getDimensionPixelSize(R.styleable.RangeSeekBar_rangeHeight, 4.dpToPx())
            rangeColor = it.getColor(R.styleable.RangeSeekBar_rangeColor, Color.RED)

            startProgress = it.getInt(R.styleable.RangeSeekBar_startPointProgress, -1)
            endProgress = it.getInt(R.styleable.RangeSeekBar_endPointProgress, -1)

            startIconDrawable = initDrawable(it.getDrawable(R.styleable.RangeSeekBar_startIconDrawable))
            endIconDrawable = initDrawable(it.getDrawable(R.styleable.RangeSeekBar_endIconDrawable))
        }.recycle()

        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        thumbIconDrawable = initDrawable(thumb)
        thumb = null

        initPadding()
    }

    private fun initDrawable(drawable: Drawable?): Drawable {
        return drawable ?: ContextCompat.getDrawable(context, android.R.drawable.ic_delete)!!
    }

    private fun initPadding() {
        originPaddingStart = paddingStart
        originPaddingEnd = paddingEnd
        originPaddingTop = paddingTop
        originPaddingBottom = paddingBottom

        iconPaddingStartEnd = max(max(startIconDrawable.intrinsicWidth, endIconDrawable.intrinsicWidth), thumbIconDrawable.intrinsicWidth) / 2
        iconPaddingTopBottom = max(max(startIconDrawable.intrinsicHeight, startIconDrawable.intrinsicHeight), startIconDrawable.intrinsicHeight)

        val realPaddingStart = max(originPaddingStart, iconPaddingStartEnd)
        val realPaddingEnd = max(originPaddingEnd, iconPaddingStartEnd)
        val realPaddingTop = originPaddingTop + iconPaddingTopBottom
        val realPaddingBottom = originPaddingBottom + iconPaddingTopBottom

        setPadding(realPaddingStart, realPaddingTop, realPaddingEnd, realPaddingBottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawIcons(it)
            drawRange(it)
            drawThumb(it)
        }
    }

    private fun drawIcons(canvas: Canvas) {
        if (startProgress != -1 && !isLastDrawStart)
            drawIconDrawable(canvas, startIconDrawable, startProgress, startOffsetStart, startOffsetTop)

        if (endProgress != -1)
            drawIconDrawable(canvas, endIconDrawable, endProgress, endOffsetStart, endOffsetTop)

        if (startProgress != -1 && isLastDrawStart)
            drawIconDrawable(canvas, startIconDrawable, startProgress, startOffsetStart, startOffsetTop)
    }

    private fun drawRange(canvas: Canvas) {
        if (startProgress != -1 && endProgress != -1) {
            val left = getProgressStartX(min(startProgress, endProgress))
            val top = height / 2 - rangeHeight / 2
            val right = left + min(abs(getProgressStartX(endProgress) - getProgressStartX(startProgress)), width)
            val bottom = top + rangeHeight

            if (width != 0) {
                ColorDrawable(rangeColor).apply { setBounds(left, top, right, bottom) }.draw(canvas)
            }
        }
    }

    private fun drawThumb(canvas: Canvas) {
        drawIconDrawable(canvas, thumbIconDrawable, progress, thumbOffsetStart, thumbOffsetTop)
    }

    private fun drawIconDrawable(canvas: Canvas, drawable: Drawable, progress: Int, offsetStart: Int, offsetTop: Int) {
        val left = getProgressStartX(progress) - drawable.intrinsicWidth / 2 + offsetStart
        val top = height / 2 - drawable.intrinsicHeight / 2 + offsetTop

        drawable.setBounds(left, top, left + drawable.intrinsicWidth, top + drawable.intrinsicHeight)
        canvas.also { drawable.draw(it) }
    }

    private fun getProgressStartX(progress: Int): Int {
        val padding: Int = paddingLeft + paddingRight
        val sPos: Int = paddingLeft

        return ((width - padding) * progress.toLong() / (if (max != 0) max else 100) + sPos).toInt()
    }

    fun checkStart(progress: Int) {
        isLastDrawStart = true
        startProgress = progress
        invalidate()
    }

    fun checkEnd(progress: Int) {
        isLastDrawStart = false
        endProgress = progress
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.startProgress = startProgress
        ss.endProgress = endProgress
        ss.isLastDrawStart = isLastDrawStart

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)

        startProgress = state.startProgress
        endProgress = state.endProgress
        isLastDrawStart = state.isLastDrawStart ?: false
    }

    class SavedState : BaseSavedState {
        var startProgress: Int = -1
        var endProgress: Int = -1
        var isLastDrawStart: Boolean? = null

        constructor(source: Parcel?) : super(source) {
            startProgress = source?.readInt() ?: -1
            endProgress = source?.readInt() ?: -1
            isLastDrawStart = source?.readInt() == 1
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeInt(startProgress)
            parcel.writeInt(endProgress)
            parcel.writeInt(if (isLastDrawStart == true) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
