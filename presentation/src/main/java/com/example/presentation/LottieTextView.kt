package com.example.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.widget.TextView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.presentation.custom.drawable.ShadowDrawable

class LottieTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private val lottieComposition = LottieCompositionFactory.fromAssetSync(context, "anim_diamond3.json").value
    private var shadowDrawable: ShadowDrawable? = null
    private var lottieDrawable: LottieDrawable? = null
    private var layerDrawable: LayerDrawable? = null
    private var _text: CharSequence? = null

    init {
        createLottieDrawable()
    }

    fun upsertText(text: CharSequence) {
        _text = text
        this.text = combineLottieAndText()
    }

    fun startAnimation() {
        lottieDrawable?.playAnimation()
        shadowDrawable?.start()

    }

    fun endAnimation() {
        lottieDrawable?.endAnimation()
        shadowDrawable?.stop()
    }

    private fun createLottieDrawable(): Drawable {
        ShadowDrawable(context).apply {

        }.also {            setMyDrawable(it) }
        LottieDrawable().apply {
            composition = lottieComposition
            repeatCount = 10
            scale = 0.5f
        }.also { setLottieDrawable(it) }

        return LayerDrawable(arrayOf(shadowDrawable,lottieDrawable)).also { setLayerDrawable(it) }

        /*return LottieDrawable().apply {
            composition = lottieComposition
            repeatCount = 10
            scale = 0.5f
        }.also { setLottieDrawable(it) }*/
    }

    private fun setLottieDrawable(drawable: LottieDrawable) {
        this.lottieDrawable = drawable
        drawable.callback = this
    }

    private fun setMyDrawable(drawable: ShadowDrawable) {
        this.shadowDrawable = drawable
        drawable.callback = this
    }

    private fun setLayerDrawable(drawable: LayerDrawable) {
        this.layerDrawable = drawable
        drawable.callback = this
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)

        if (drawable == lottieDrawable) {
            text = combineLottieAndText()
        }
    }

    private fun combineLottieAndText(): Spannable {
        val lottie = SpannableStringBuilder().append(" ").apply {
            setSpan(CustomImageSpan(shadowDrawable, CustomImageSpan.ALIGN_CENTER), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return SpannableStringBuilder().apply {
            append(lottie)
            append(_text)
        }
    }
}