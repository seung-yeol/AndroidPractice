package com.example.presentation.fragment.visibility

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_visible.*

class VisibleFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_visible, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        orangeOnOff.setOnClickListener {
            orange.visibility = if (orange.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        blueOnOff.setOnClickListener {
            blue.visibility = if (blue.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }
}