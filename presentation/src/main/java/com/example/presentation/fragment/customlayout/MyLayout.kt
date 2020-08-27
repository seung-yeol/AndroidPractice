package com.example.presentation.fragment.customlayout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.OverScroller
import androidx.core.view.*
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class MyLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private val configuration = ViewConfiguration.get(context)
    private val mMaximumVelocity = configuration.scaledMaximumFlingVelocity.toFloat()
    private val touchSlop = configuration.scaledTouchSlop
    private val viewAndScrollerList = LinkedList<Pair<View, OverScroller>>()

    private var standByClick = true
    private var isDrag = false
    private var downX = 0f
    private var downY = 0f
    private var preX = 0f
    private var preY = 0f

    private var mVelocityTracker: VelocityTracker? = null
    private var targetView: View? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!viewAndScrollerList.isNullOrEmpty()) computeFling()
    }

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
                        if (myWidth < tempWidth) myWidth = tempWidth
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

    @SuppressLint("Recycle", "ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                targetView = getLastChild(event)
                if (targetView != null) {
                    preX = event.x
                    preY = event.y

                    viewAndScrollerList.firstOrNull { it.first == targetView }
                        ?.second?.forceFinished(true)
                }

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val diffX = abs(downX - event.x)
                val diffY = abs(downY - event.y)

                if ((diffX + diffY) / 1.4f > touchSlop) {
                    isDrag = true
                    standByClick = true
                }

                if (isDrag) {
                    targetView?.also {
                        val maxX = width.toFloat() - it.width
                        val maxY = width.toFloat() - it.width
                        x = max(0f, min(maxX, x + event.x - preX))
                        y = max(0f, min(maxY, y + event.y - preY))

                        preX = event.x
                        preY = event.y
                    }
                }

                return true
            }

            MotionEvent.ACTION_UP -> {
                val result = targetView != null

                if (result && isDrag) {
                    val velocityTracker =
                        mVelocityTracker!!.apply { computeCurrentVelocity(1000, mMaximumVelocity) }
                    val xVelocity = velocityTracker.xVelocity
                    val yVelocity = velocityTracker.yVelocity

                    fling(xVelocity, yVelocity)
                }

                if (standByClick) {
                    children.findLast { child ->
                        Rect().also { child.getHitRect(it) }
                            .run { contains(event.x.toInt(), event.y.toInt()) }
                            .let { if (it) child.performClick() else false }
                    } ?: performClick()
                }

                resetTouch()

                return result
            }

            MotionEvent.ACTION_CANCEL -> {
                resetTouch()
            }
        }

        return false
    }

    private fun fling(xVelocity: Float, yVelocity: Float) {
        val scroller = OverScroller(context)
        scroller.fling(
            targetView!!.x.toInt(),
            targetView!!.y.toInt(),
            xVelocity.toInt(),
            yVelocity.toInt(),
            0,
            width - targetView!!.width,
            0,
            height - targetView!!.height
        )
        viewAndScrollerList.add(targetView!! to scroller)
        computeFling()
    }

    private fun resetTouch() {
        targetView = null
        preX = 0f
        preY = 0f

        standByClick = true
        isDrag = false
        mVelocityTracker?.recycle()
        mVelocityTracker = null
        parent.requestDisallowInterceptTouchEvent(false)
    }

    @SuppressLint("Recycle")
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var result = isChild(ev)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                targetView = getLastChild(ev)

                downX = ev.x
                downY = ev.y

                result = false
            }

            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                return if (targetView != null)
                    canDrag(this, ev.x, ev.y) == DraggableState.POSSIBLE
                else false
            }

            MotionEvent.ACTION_UP -> return false
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        return result
    }

    enum class DraggableState {
        POSSIBLE, IMPOSSIBLE, STATELESS
    }

    open fun canDrag(
        view: View,
        touchX: Float,
        touchY: Float
    ): DraggableState {
        if (view is ViewGroup && view.childCount != 0) {
            view.children.filter { child ->
                child.x <= touchX && touchX <= child.x + child.width && child.y <= touchY && touchY <= child.y + child.height
            }.toList().let {
                if (it.isNullOrEmpty()) {
                    return DraggableState.IMPOSSIBLE
                } else {
                    it.forEach { view ->
                        val canDrag = DraggableState.IMPOSSIBLE == canDrag(
                            view,
                            touchX - view.x,
                            touchY - view.y
                        )

                        return if (canDrag) DraggableState.POSSIBLE else DraggableState.STATELESS
                    }
                }
            }
        }
        return DraggableState.IMPOSSIBLE
    }

    private fun computeFling() {
        viewAndScrollerList.removeAll(viewAndScrollerList.filter { it.second.isFinished })
        viewAndScrollerList.forEach {
            val view = it.first
            val scroller = it.second
            scroller.computeScrollOffset()
            view.x = scroller.currX.toFloat()
            view.y = scroller.currY.toFloat()
        }

        postInvalidateOnAnimation()
    }

    private fun isChild(ev: MotionEvent): Boolean {
        return children.any { child ->
            Rect().also {
                child.getHitRect(it)
            }.run {
                contains(ev.x.toInt(), ev.y.toInt())
            }
        }
    }

    private fun getLastChild(ev: MotionEvent): View? {
        return children.lastOrNull { child ->
            Rect().also {
                child.getHitRect(it)
            }.run {
                contains(ev.x.toInt(), ev.y.toInt())
            }
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}