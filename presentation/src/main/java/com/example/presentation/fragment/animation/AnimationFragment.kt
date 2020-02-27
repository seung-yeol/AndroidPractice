package com.example.presentation.fragment.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_animation.*

class AnimationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_animation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image.apply {
            pivotX = 0f
            pivotY = 0f
        }
        val anim = ObjectAnimator.ofPropertyValuesHolder(
            image,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.1f),
            PropertyValuesHolder.ofFloat(View.SCALE_X, 10f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 10f),
            PropertyValuesHolder.ofFloat(View.X, 500f),
            PropertyValuesHolder.ofFloat(View.Y, 500f)
        ).apply {
            duration = 5000L
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    println("OSY animation end")
                }
            })
        }

        btnStart.setOnClickListener {
            anim.start()
        }

        btnPause.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                anim.pause()
            }
        }

        btnResume.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                anim.resume()
            }
        }

        btnCancel.setOnClickListener {
            anim.cancel()
        }

        btnEnd.setOnClickListener {
            anim.end()
        }
    }
}