package com.example.presentation.fragment.eventdriven

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_event_driven.*

class EventCatchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_driven, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        eventDrivenView.setEventDrawables(listOf(ContextCompat.getDrawable(requireContext(), R.drawable.img_santa_1)!!, ContextCompat.getDrawable(requireContext(), R.drawable.img_santa_2)!!))
        eventDrivenView.setOnEventClickListener {
            Toast.makeText(context, "개짖는 소리 좀 안나게 하라", Toast.LENGTH_SHORT).show()
        }
        eventStartButton.setOnClickListener {
            eventDrivenView.startEvent()
        }
    }
}