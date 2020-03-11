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
import com.example.presentation.fragment.drawable.drawable.ShadowDrawable2
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_drawable.*

class DrawableFragment2 : Fragment() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drawable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shadowDrawable = ShadowDrawable2(requireContext())
        val shadowDrawable2 = ShadowDrawable2(requireContext())
        var lottieDrawable: LottieDrawable? = null

        Single.create<LottieComposition> {
            it.onSuccess(
                LottieCompositionFactory.fromAssetSync(
                    context,
                    "anim_diamond3.json"
                ).value!!
            )
        }.observeOn(AndroidSchedulers.mainThread()).subscribe({
            lottieDrawable = LottieDrawable().apply {
                composition = it
                repeatCount = 1
                addAnimatorUpdateListener {
                    shadowDrawable.setProgress(it.animatedFraction)
                    shadowDrawable2.setProgress(it.animatedFraction)
                }
            }

            val comDrawable = LayerDrawable(arrayOf(shadowDrawable, lottieDrawable))
            centerImage5.setImageDrawable(comDrawable)
        }, { }).let {
            compositeDisposable.add(it)
        }

        centerImage.setImageDrawable(shadowDrawable2)
        startBtn.setOnClickListener {
            lottieDrawable?.playAnimation()
        }

        endBtn.setOnClickListener {
            lottieDrawable?.endAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}