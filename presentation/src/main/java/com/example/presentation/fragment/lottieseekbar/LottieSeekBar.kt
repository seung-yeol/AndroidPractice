/*
package com.example.presentation.fragment.lottieseekbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieDrawable.INFINITE
import com.example.presentation.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.lottie_seekbar_view.view.*

class LottieSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "TimeDealTimerView"
    }

    private var disposables = CompositeDisposable()

    private enum class AnimationType {
        NONE, NORMAL, FAST
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.lottie_seekbar_view, this, true)

//        initView()
    }

    private fun initView() {
        LottieDrawable().apply {
            repeatCount = 1
            addAnimatorUpdateListener {
//                shadowAnimationDrawable.setProgress(it.animatedFraction)
            }
        }.also { lottie ->
            LottieCompositionFactory.fromAsset(context, "anim_diamond3.json").addListener { lottieComposition ->
                lottie.composition = lottieComposition

                */
/*LayerDrawable(arrayOf(shadowAnimationDrawable, lottie))
                        .apply {
                            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                            callback = seekBar
                        }*//*


            }
        }.also {
            seekBar.thumb = it
            it.repeatCount = INFINITE
            it.playAnimation()
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible) {
            disposables.dispose()
            disposables = CompositeDisposable()
            observe()
        } else {
            disposables.dispose()
        }
    }

    private fun observe() {

    }
}*/
