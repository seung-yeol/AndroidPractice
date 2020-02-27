package com.example.presentation.fragment.drawable

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.presentation.R
import com.example.presentation.fragment.drawable.drawable.ShadowDrawable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_drawable.*

class DrawableFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drawable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shadowDrawable = ShadowDrawable(requireContext())
        val shadowDrawable2 = ShadowDrawable(requireContext())
        var smallLottie: LottieDrawable? = null
        centerImage.setImageDrawable(shadowDrawable2)

        Single.create<LottieComposition> {
            it.onSuccess(
                LottieCompositionFactory.fromAssetSync(
                    context,
                    "anim_diamond3.json"
                ).value!!
            )
        }.observeOn(AndroidSchedulers.mainThread()).subscribe({
            smallLottie = LottieDrawable().apply {
                composition = it
                repeatCount = 1
            }

            val comDrawable = LayerDrawable(arrayOf(shadowDrawable, smallLottie))
            centerImage5.setImageDrawable(comDrawable)
        }, { }).let {
            compositeDisposable.add(it)
        }

        centerImage.setImageDrawable(shadowDrawable2)
        startBtn.setOnClickListener {
            smallLottie?.playAnimation()
            shadowDrawable.start()
            shadowDrawable2.start()
        }

        endBtn.setOnClickListener {
            smallLottie?.endAnimation()
            shadowDrawable.stop()
            shadowDrawable2.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}