package com.example.presentation.fragment.start

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class MyItemAnimator : DefaultItemAnimator() {
    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        val view = holder!!.itemView
        view.translationY = -view.height.toFloat()
        println("OSY asd ${-view.height.toFloat()}")
        ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f).setDuration(2000).start()

        return true
    }
}