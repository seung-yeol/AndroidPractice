package com.example.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_range_seek_bar5.*

class RangeSeekBar2Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_range_seek_bar5, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        startBtn.setOnClickListener {
            rangeSeekBar.checkStart(rangeSeekBar.progress)
        }

        endBtn.setOnClickListener {
            rangeSeekBar.checkEnd(rangeSeekBar.progress)
        }
    }
}