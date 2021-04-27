package com.example.presentation.fragment.slotmachinetext

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.Animation
import android.widget.TextView
import android.widget.ViewFlipper
import com.example.presentation.R
import com.osy.util.toSp

class TextFlipper @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewFlipper(context, attrs) {

    companion object {
        private val TAG = "TextFlipper"
    }

    private var gravity: Int
    private var textStyle: Int
    private var textSize: Int
    private var textColor: Int

    private var index = 0
    private var texts: List<String> = emptyList()

    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TextFlipper)
        gravity = attributes.getInt(R.styleable.TextFlipper_android_gravity, 0)
        textSize = attributes.getDimensionPixelSize(R.styleable.TextFlipper_android_textSize, 0)
        textStyle = attributes.getInt(R.styleable.TextFlipper_android_textStyle, 0)
        textColor = attributes.getColor(R.styleable.TextFlipper_android_textColor, Color.BLACK)

        attributes.recycle()
        addTextViews()
        isChildrenDrawingOrderEnabled = true
    }

    private fun addTextViews() {
        addView(generateTextView())
        addView(generateTextView())
        addView(generateTextView())
    }

    private fun generateTextView(): TextView {
        return TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            gravity = this@TextFlipper.gravity
            setTextSize(TypedValue.COMPLEX_UNIT_PX, this@TextFlipper.textSize.toFloat())
            setTextColor(textColor)
            typeface = Typeface.defaultFromStyle(textStyle)
        }
    }

    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        return if (displayedChild == 0) (childCount - 1 - drawingPosition) else drawingPosition
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return super.generateDefaultLayoutParams().apply { gravity = this@TextFlipper.gravity }
    }

    fun setTexts(texts: List<String>?) {
        if (this.texts != texts) {
            stopFlipping()
            this.texts = texts.orEmpty()
            index = 0
            setDisplayedIndex(index)
            if (isAutoStart) {
                startFlipping()
            }
        }
    }

    override fun startFlipping() {
        if (this.texts.size > 1) {
            super.startFlipping()
            if (!animateFirstView) {
                getDisplayedView()?.clearAnimation()
            }
        }
    }

    override fun setDisplayedChild(whichChild: Int) {
        super.setDisplayedChild(whichChild)
        setDisplayedIndex(getNextIndex())
    }

    private fun setDisplayedIndex(index: Int) {
        val textsSize = texts.size
        if (textsSize == 0) {
            val displayedView = getDisplayedView() as TextView?
            displayedView?.text = null
            return
        }

        val displayedView = getDisplayedView() as TextView?
        val text = texts[index]
        displayedView?.text = text
        this.index = index

        val nextView = getNextView() as TextView?
        val nextText = texts[getNextIndex()]
        nextView?.text = nextText
    }

    private fun getDisplayedView(): View? {
        return getViewSafely(displayedChild)
    }

    private fun getNextView(): View? {
        return getViewSafely(displayedChild + 1)
    }

    private fun getViewSafely(whichChild: Int): View? {
        var resultWhichChild = whichChild
        if (resultWhichChild >= childCount) {
            resultWhichChild = 0
        } else if (resultWhichChild < 0) {
            resultWhichChild = childCount - 1
        }
        return getChildAt(resultWhichChild)
    }

    private fun getNextIndex(): Int {
        return getIndexSafely(index + 1)
    }

    private fun getIndexSafely(index: Int): Int {
        var resultIndex = index
        val imageUriSize = texts.size
        if (resultIndex >= imageUriSize) {
            resultIndex = 0
        } else if (resultIndex < 0) {
            resultIndex = imageUriSize - 1
        }
        return resultIndex
    }
}