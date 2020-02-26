package com.example.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R

class RangeSeekBarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_range_seek_bar4, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
    }

    private fun init() {
        /*rangeSeekBar?.leftSeekBar?.thumbDrawableId = R.drawable.thumb_yellow
        rangeSeekBar?.rightSeekBar?.thumbDrawableId = R.drawable.thumb_yellow

        rangeSeekBar.leftSeekBar.indicatorDrawableId = R.drawabl    e.img_make_highlight_start_copy_3
        rangeSeekBar.rightSeekBar.indicatorDrawableId = R.drawable.img_make_highlight_end_copy_9*/
    }

    override fun onResume() {
        super.onResume()
    }
}