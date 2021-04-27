package com.example.presentation.fragment.lottieseekbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.example.presentation.R
import com.osy.util.dpToPx
import kotlinx.android.synthetic.main.group_purchase_progressbar_view.view.*

class VerticalLottieSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var layerDrawable: LayerDrawable? = null
    private var thumbHeight: Int = 123.5f.dpToPx()
    private var thumbLottieFileName: String? = null

    private var rangeColor: Int = Color.RED
    private var rangeHeight: Int = 4.dpToPx()

    init {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.group_purchase_progressbar_view, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.VerticalLottieSeekBar).also {
            thumbLottieFileName = it.getString(R.styleable.VerticalLottieSeekBar_thumb_lottie_fileName)

            rangeHeight = it.getDimensionPixelSize(R.styleable.VerticalLottieSeekBar_rangeWidth, 4.dpToPx())
            rangeColor = it.getColor(R.styleable.VerticalLottieSeekBar_rangeColor, Color.RED)

            progressBar.max = it.getInt(R.styleable.VerticalLottieSeekBar_max, 100)
        }.recycle()

        initThumb()
        initFireView()
    }

    private fun initThumb() {
        LottieDrawable().apply {
            repeatCount = INFINITE
        }.also { lottie ->
            LottieCompositionFactory.fromAsset(context, "live_grouppurchase_fire.json").addListener { lottieComposition ->
                lottie.composition = lottieComposition
                lottie.playAnimation()

                layerDrawable = LayerDrawable(arrayOf(ContextCompat.getDrawable(context, R.drawable.img_live_grouppurchase_fire_blur), lottie))
                    .apply {
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                        callback = fireView
                    }

                fireView.setImageDrawable(layerDrawable)
                fireView.visibility = VISIBLE
                moveThumb()
            }
        }
    }

    private fun initFireView() {
    }

    private fun initPadding() {
        progressBar.setPadding(0, thumbHeight / 2, 0, thumbHeight / 2)
    }

    fun setProgress(progress: Int) {
        progressBar.progress = progress
        moveThumb()
    }

    private val progressHeight = 135.5f.dpToPx()

    private fun moveThumb() {
//        val fireViewYPosition = paddingTop + (height - thumbHeight) * (1 - progressBar.run { progress.toFloat() / max }) - (fireView.height / 2) + 14.5f.dpToPx()
        val fireViewYPosition = progressHeight * (1 - progressBar.run { progress.toFloat() / max })

        println("osy fireViewYPosition : $fireViewYPosition   height: $height      ")

        fireView.y = fireViewYPosition
    }
}