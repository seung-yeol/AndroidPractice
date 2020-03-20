package com.example.presentation.fragment.customlayout

import android.content.Context
import android.util.AttributeSet
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

class MyLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var myWidth = 0
        var myHeight = 0

        children.forEach {
            if (it.visibility != View.GONE) {
                measureChild(it, widthMeasureSpec, heightMeasureSpec)
            }
        }

        var tempWidth = paddingLeft + paddingRight
        if (widthMode != MeasureSpec.AT_MOST) {
            myWidth = maxWidth
        } else {
            children.forEach {
                if (it.visibility != View.GONE) {
                    val viewWidth = it.measuredWidth + it.marginLeft + it.marginRight
                    if (tempWidth + viewWidth > maxWidth) {
                        if (myWidth < tempWidth) {
                            myWidth = tempWidth
                        }
                        tempWidth = paddingLeft + paddingRight + viewWidth
                    } else {
                        tempWidth += viewWidth
                    }
                }
            }
        }

        tempWidth = paddingLeft + paddingRight
        if (heightMode != MeasureSpec.AT_MOST) {
            myHeight = heightSize
        } else {
            myHeight += paddingTop + paddingBottom
            var bigY = 0
            children.forEach {
                if (it.visibility != View.GONE) {
                    val viewWidth = it.measuredWidth + it.marginLeft + it.marginRight
                    val viewHeight = it.measuredHeight + it.marginTop + it.marginBottom

                    if (tempWidth + viewWidth > maxWidth) {
                        myHeight += bigY
                        bigY = viewHeight
                        tempWidth = paddingLeft + paddingRight + viewWidth
                    } else {
                        tempWidth += viewWidth

                        if (bigY < viewHeight) {
                            bigY = viewHeight
                        }
                    }
                }
            }
            myHeight += bigY
        }

        setMeasuredDimension(myWidth, myHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var innerWidth = paddingLeft + paddingRight

        var bigY = 0
        var startX = paddingLeft
        var startY = paddingTop

        children.forEach {
            if (it.visibility != View.GONE) {
                val childWidth = it.measuredWidth
                val childHeight = it.measuredHeight

                val viewWidth = childWidth + it.marginLeft + it.marginRight
                val viewHeight = childHeight + it.marginTop + it.marginBottom

                innerWidth += viewWidth
                if (innerWidth > width) {
                    startY += bigY
                    bigY = 0
                    startX = paddingLeft
                    innerWidth = paddingLeft + paddingRight + viewWidth
                }
                if (bigY < viewHeight) bigY = viewHeight

                it.layout(
                    startX + it.marginLeft,
                    startY + it.marginTop,
                    startX + childWidth + it.marginLeft,
                    startY + childHeight + it.marginTop
                )
                startX += viewWidth
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }


    private var targetView: View? = null
    private var preX = 0f
    private var preY = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                targetView = children.lastOrNull { child ->
                    child.performClick()
                    Rect().also {
                        child.getHitRect(it)
                    }.run {
                        contains(event.x.toInt(), event.y.toInt()).also {
                            if (it) {
                                preX = event.x
                                preY = event.y
                            }
                        }
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                targetView = null
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                targetView?.apply {
                    x += event.x - preX
                    y += event.y - preY

                    preX = event.x
                    preY = event.y
                }
            }
        }

        return true
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}