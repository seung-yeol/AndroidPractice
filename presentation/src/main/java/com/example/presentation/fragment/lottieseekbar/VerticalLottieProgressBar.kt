/*
package com.example.presentation.fragment.lottieseekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewConfiguration
import android.widget.ProgressBar
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.presentation.R
import com.osy.util.dpToPx
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class VerticalLottieProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.Widget_AppCompat_ProgressBar_Horizontal
) : ProgressBar(context, attrs, defStyleAttr) {
    private var mScaledTouchSlop: Int = 0
    private var iconPaddingStartEnd: Int = 0
    private var iconPaddingTopBottom: Int = 0

    private var originPaddingStart: Int = 0
    private var originPaddingEnd: Int = 0
    private var originPaddingTop: Int = 0
    private var originPaddingBottom: Int = 0

    */
/*
      offset 값이 커질수록 아이콘이 아래 or 오른쪽으로 감.
      아이콘의 중간지점이 progress의 right | center_vertical 으로 되어 있음.
    *//*

    private var thumbOffsetStart: Int = 0
    private var thumbOffsetTop: Int = 0
    private var thumbLottieFileName: String? = null

    private var rangeColor: Int = Color.RED
    private var rangeHeight: Int = 4.dpToPx()

    private lateinit var startIconDrawable: Drawable
    private lateinit var endIconDrawable: Drawable
    private lateinit var thumbLottieDrawable: Drawable

    init {
        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.VerticalLottieProgressBar).also {
            thumbLottieFileName = it.getString(R.styleable.VerticalLottieProgressBar_thumb_lottie_fileName)
            thumbOffsetStart = it.getDimensionPixelSize(R.styleable.VerticalLottieProgressBar_thumbIconOffsetStart, 0)
            thumbOffsetTop = it.getDimensionPixelSize(R.styleable.VerticalLottieProgressBar_thumbIconOffsetTop, 0)

            rangeHeight = it.getDimensionPixelSize(R.styleable.VerticalLottieProgressBar_rangeWidth, 4.dpToPx())
            rangeColor = it.getColor(R.styleable.VerticalLottieProgressBar_rangeColor, Color.RED)
        }.recycle()

        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        initThumb()
        initPadding()
    }

    private fun initThumb() {
        thumbLottieDrawable = LottieDrawable().apply {
            repeatCount = 1
            addAnimatorUpdateListener {
//                shadowAnimationDrawable.setProgress(it.animatedFraction)
            }
        }.also { lottie ->
            LottieCompositionFactory.fromAsset(context, "anim_diamond3.json").addListener { lottieComposition ->
                lottie.composition = lottieComposition
                lottie.callback = this

                */
/*layerDrawable = LayerDrawable(arrayOf(shadowAnimationDrawable, lottie))
                    .apply {
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                        callback = this@DiamondMessageView
                    }*//*

            }
        }
    }

    private fun initPadding() {
        originPaddingStart = paddingStart
        originPaddingEnd = paddingEnd
        originPaddingTop = paddingTop
        originPaddingBottom = paddingBottom

        iconPaddingStartEnd = max(max(startIconDrawable.intrinsicWidth, endIconDrawable.intrinsicWidth), thumbLottieDrawable.intrinsicWidth) / 2
        iconPaddingTopBottom = max(max(startIconDrawable.intrinsicHeight, startIconDrawable.intrinsicHeight), startIconDrawable.intrinsicHeight)

        val realPaddingStart = max(originPaddingStart, iconPaddingStartEnd)
        val realPaddingEnd = max(originPaddingEnd, iconPaddingStartEnd)
        val realPaddingTop = originPaddingTop + iconPaddingTopBottom
        val realPaddingBottom = originPaddingBottom + iconPaddingTopBottom

        setPadding(realPaddingStart, realPaddingTop, realPaddingEnd, realPaddingBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawThumb(canvas)
    }

    */
/*private fun drawTrack(canvas: Canvas) {
        if (startProgress != -1 && endProgress != -1) {
            val left = getProgressStartX(min(startProgress, endProgress))
            val top = height / 2 - rangeHeight / 2
            val right = left + min(abs(getProgressStartX(endProgress) - getProgressStartX(startProgress)), width)
            val bottom = top + rangeHeight

            if (width != 0) {
                ColorDrawable(rangeColor).apply { setBounds(left, top, right, bottom) }.draw(canvas)
            }
        }
    }*//*


    private fun drawThumb(canvas: Canvas) {
        drawIconDrawable(canvas, thumbLottieDrawable, progress, thumbOffsetStart, thumbOffsetTop)
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

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)

//        startProgress = state.startProgress
//        endProgress = state.endProgress
//        isLaterDrawStartIcon = state.isLastDrawStart ?: true
    }

    class SavedState : BaseSavedState {

        constructor(source: Parcel?) : super(source) {
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
//            parcel.writeInt(startProgress)
//            parcel.writeInt(endProgress)
//            parcel.writeInt(if (isLastDrawStart == true) 1 else 0)
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
*/
