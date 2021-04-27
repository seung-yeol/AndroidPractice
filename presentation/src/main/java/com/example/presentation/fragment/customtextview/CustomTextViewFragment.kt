package com.example.presentation.fragment.customtextview

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_custom_text_layout.*

class CustomTextViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom_text_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*myTextView.apply {
            setTextColor(Color.BLUE)
            gravity = left
            text = "저희 집 동물들이 참 12345678910마넝리ㅏㅓ ㅁ니ㅏㅇasdfasdfavzxcv러vczvbadsfgsdfgsdfㅣ ㅁ나ㅓ이라ㅓ ㅣㅏ머 ㅣ나어리"
        }
        myTextView4.apply {
            setTextColor(Color.BLUE)
            gravity = left
            text = "저희 집 동물들이 참 12345678910마넝리ㅏㅓ ㅁ니ㅏㅇasdfasdfavzxcv러vczvbadsfgsdfgsdfㅣ ㅁ나ㅓ이라ㅓ ㅣㅏ머 ㅣ나어리"
        }
        myTextView5.apply {
            setTextColor(Color.BLUE)
            gravity = left
            text = "저희 집 동물들이 참 12345678910마넝리ㅏㅓ ㅁ니ㅏㅇasdfasdfavzxcv러vczvbadsfgsdfgsdfㅣ ㅁ나ㅓ이라ㅓ ㅣㅏ머 ㅣ나어리"
        }*/

        myTextView2.apply {
            setTextColor(Color.BLUE)
            gravity = left


            text = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세보전하세보전하세보전하세보전하세보전하세보전하세보전하세보전하세"
        }


        myTextView3.apply {
            setTextColor(Color.BLUE)
            gravity = Gravity.CENTER_HORIZONTAL
            text = "동해물과 백두\n산\n이 마르고 \n닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세"
        }


        /*myTextView4.apply {
//            setTopLine(2)
//            setTopTextSize(16f)
//            setTopTextColor(Color.BLUE)
//            setBottomTextSize(13f)
//            setBottomTextColor(Color.RED)
            setText("동해물과\n 백두산이\n 마르고 \n닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하셍")
        }

        myTextView2.apply {
//            setTopTextSize(20f)
//            setTopTextColor(Color.BLACK)
//            setBottomTextSize(12f)
//            setBottomTextColor(Color.RED)
            setText("동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하셍")
        }

        myTextView3.apply {
//            setTopTextSize(13f)
//            setTopTextColor(Color.CYAN)
//            setBottomTextSize(18f)
//            setBottomTextColor(Color.RED)
            setText("동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하셍 동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하셍")
        }

        myTextView5.apply {
//            setTopTextSize(19f)
//            setTopTextColor(Color.CYAN)
//            setBottomTextSize(18f)
//            setBottomTextColor(Color.YELLOW)
            setText("동해물과 백두산이 마르고 닳도록 하느님이 보우하사")
        }*/
    }
}