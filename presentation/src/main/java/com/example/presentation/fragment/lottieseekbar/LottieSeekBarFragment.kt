package com.example.presentation.fragment.lottieseekbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_lottie_seek_bar.*

class LottieSeekBarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lottie_seek_bar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private var progress = 0

    private fun init() {
        nextBtn.setOnClickListener {
            if (progress != 10) {
                progress++
                lottieProgress.setProgress(progress)
            }
        }

        prevBtn.setOnClickListener {
            if (progress != 0) {
                progress--
                lottieProgress.setProgress(progress)
            }
        }
    }
}