package com.example.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.max

class OSYViewLayout @JvmOverloads constructor(
    context: Context, val attrs: AttributeSet? = null, val defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    interface OnPageSelectedListener {
        fun onPageSelected(position: Int)
    }

    interface OnPageScrollStateChangedListener {
        fun onPageScrollStateChanged(state: Int)
    }

    interface OnPageScrolledListener {
        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
    }

    private val viewPager: ViewPager2 = ViewPager2(context, attrs, defStyleAttr)
    private var osyAdapter: OSYViewPagerAdapter = OSYViewPagerAdapter()

    private var swipeable = true
    private val mTmpContainerRect = Rect()

    private val mTmpChildRect = Rect()

    private var pagerChildCount = 0
    var onPageSelectedListener: OnPageSelectedListener? = null
    var onPageScrollStateChangedListener: OnPageScrollStateChangedListener? = null
    var onPageScrolledListener: OnPageScrolledListener? = null

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.OSYViewLayout, 0, 0)
        swipeable = a.getBoolean(R.styleable.OSYViewLayout_swipeable, true)
        initialize()
    }


    private fun initialize() {
        println("OSY initialize")
        viewPager.adapter = osyAdapter
        viewPager.isUserInputEnabled = swipeable
        viewPager.setBackgroundColor(Color.TRANSPARENT)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageSelectedListener?.onPageSelected(position)
                println("OSY page : $position    count : ${osyAdapter.itemCount}")
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                onPageScrollStateChangedListener?.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                onPageScrolledListener?.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }
        })
        viewPager.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        attachViewToParent(viewPager, 0, viewPager.layoutParams)
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        super.addView(child, index, params)
        println("OSY addView index")
    }

    override fun addView(child: View?) {
        super.addView(child)
        println("OSY addView ")
    }

    override fun onViewAdded(child: View?) {
        pagerChildCount++
        osyAdapter.addView(child)
        if (child is TextView) println("OSY onViewAdded ${child.text}")
    }

    override fun onFinishInflate() {
        println("OSY onFinishInflate")
        super.onFinishInflate()
    }

    override fun onAttachedToWindow() {
        println("OSY onAttachedToWindow")
        super.onAttachedToWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        println("OSY onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measureChild(viewPager, widthMeasureSpec, heightMeasureSpec)
        var width = viewPager.measuredWidth
        var height = viewPager.measuredHeight
        val childState = viewPager.measuredState

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        width = max(width, suggestedMinimumWidth)
        height = max(height, suggestedMinimumHeight)

        setMeasuredDimension(
            View.resolveSizeAndState(width, widthMeasureSpec, childState),
            View.resolveSizeAndState(
                height, heightMeasureSpec,
                childState shl View.MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        println("OSY onLayout")
        val width = viewPager.measuredWidth
        val height = viewPager.measuredHeight

        mTmpContainerRect.left = paddingLeft
        mTmpContainerRect.right = r - l - paddingRight
        mTmpContainerRect.top = paddingTop
        mTmpContainerRect.bottom = b - t - paddingBottom

        Gravity.apply(Gravity.TOP or Gravity.START, width, height, mTmpContainerRect, mTmpChildRect)

        detachViewsFromParent(1, pagerChildCount)
        viewPager.layout(
            mTmpChildRect.left, mTmpChildRect.top, mTmpChildRect.right, mTmpChildRect.bottom
        )
    }
}