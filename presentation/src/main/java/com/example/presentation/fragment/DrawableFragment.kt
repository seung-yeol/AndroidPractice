package com.example.presentation.fragment

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.presentation.R
import com.example.presentation.custom.drawable.ShadowDrawable
import kotlinx.android.synthetic.main.fragment_drawable.*

class DrawableFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drawable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lottieComposition2 =
            LottieCompositionFactory.fromAssetSync(context, "anim_diamond3.json").value
        val smallLottie = LottieDrawable().apply {
            composition = lottieComposition2
            repeatCount = 1
        }

        val shadowDrawable = ShadowDrawable(requireContext())
        val shadowDrawable2 = ShadowDrawable(requireContext())
        val comDrawable = LayerDrawable(arrayOf(shadowDrawable, smallLottie))

        centerImage.setImageDrawable(shadowDrawable2)
        centerImage5.setImageDrawable(comDrawable)


        startBtn.setOnClickListener {
            smallLottie.playAnimation()
            shadowDrawable.start()
            shadowDrawable2.start()
        }

        endBtn.setOnClickListener {
            smallLottie.endAnimation()
            shadowDrawable.stop()
            shadowDrawable2.stop()
        }
    }
}