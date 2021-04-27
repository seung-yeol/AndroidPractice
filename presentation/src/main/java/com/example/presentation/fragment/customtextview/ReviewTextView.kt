package com.example.presentation.fragment.customtextview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.presentation.R
import com.osy.util.spToPx
import kotlin.math.max
import kotlin.math.min

class ReviewView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {
    private var review: String = ""
    private var subText: String = ""
    private var subTextSize: Float
    private var subTextColor: Int
    private var subTextAlign: Int
    private var subTextLines: Int
    private var subTextMarginTop: Int
    private var subTextLineSpacingExtra: Float

    private val measuringPaint: TextPaint
    private val measuringBox = Rect()

    private val originPaddingBottom: Int

    init {
        val attrs: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ReviewView)
        subTextSize = attrs.getDimension(R.styleable.ReviewView_subTextSize, 12.spToPx().toFloat())
        subTextColor = attrs.getColor(R.styleable.ReviewView_subTextColor, Color.BLACK)
        subTextAlign = attrs.getInt(R.styleable.ReviewView_subTextAlign, Paint.Align.LEFT.ordinal)
        subTextLines = attrs.getInt(R.styleable.ReviewView_subTextLines, 0)
        subTextMarginTop = attrs.getDimensionPixelSize(R.styleable.ReviewView_subTextMarginTop, 0) + (textSize / 3).toInt()
        subTextLineSpacingExtra = attrs.getDimension(R.styleable.ReviewView_subTextLineSpacingExtra, 0f)

        measuringPaint = TextPaint(paint)
        originPaddingBottom = paddingBottom
        attrs.recycle()
    }

    private var subTextCurrentLines = 0
    private var subTextStrings: List<String> = listOf()
    private var subTextHeight: Float = 0f
    private var isSubTextDraw: Boolean = false
    private var splitUpText: List<String> = listOf()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        measuringPaint.textSize = textSize
        val width = min(maxWidth.toFloat(), max(width, measuredWidth).toFloat()) - paddingLeft - paddingRight
        val splitText = splitWordsIntoStringsThatFit(review, width, measuringPaint).orEmpty().filter { it.isNotBlank() }
        if (splitText != splitUpText) {
            splitUpText = splitText

            if (splitUpText.size <= maxLines) {
                isSubTextDraw = false

                text = splitUpText.joinToString("\n")
                setPadding(paddingLeft, paddingTop, paddingRight, originPaddingBottom)
            } else {
                isSubTextDraw = true
                val (splitUpText, position) = getTextWithEndPosition(review, width, maxLines - subTextLines, measuringPaint)
                subText = review.substring(position, review.length)
                text = splitUpText.joinToString("\n")

                measuringPaint.textSize = subTextSize
                measuringPaint.textAlign = Paint.Align.values()[subTextAlign]

                subTextStrings = (splitWordsIntoStringsThatFit(subText, width, measuringPaint) ?: listOf()).filter { it.isNotEmpty() }
                subTextCurrentLines = min(subTextLines, subTextStrings.size)
                subTextHeight = subTextSize + subTextLineSpacingExtra

                setPadding(paddingLeft, paddingTop, paddingRight, originPaddingBottom + subTextMarginTop + (subTextHeight * subTextCurrentLines).toInt())
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setReview(text: CharSequence) {
        review = text.toString()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { if (subTextCurrentLines > 0 && isSubTextDraw) drawSubText(canvas) }
    }

    private fun drawSubText(canvas: Canvas) {
        val isEllipsize = subTextStrings.size > subTextLines

        measuringPaint.textSize = subTextSize
        measuringPaint.color = subTextColor
        subTextStrings.take(subTextLines).forEachIndexed { index, text ->
            val ellipsizeCheckedText =
                if (isEllipsize && index == subTextLines - 1 && text.length > 4)
                    text.replaceRange(text.length - 4 until text.length, "..")
                else text

            measuringPaint.getTextBounds(ellipsizeCheckedText, 0, ellipsizeCheckedText.length - 1, measuringBox)

            val x = if (subTextAlign == 0) 0f else canvas.width.toFloat() / 2
            val y = canvas.height.toFloat() - (subTextHeight * (subTextCurrentLines - index)) + subTextLineSpacingExtra + originPaddingBottom
            canvas.drawText(ellipsizeCheckedText, 0, ellipsizeCheckedText.length, x, y + subTextMarginTop, measuringPaint)
        }
    }

    private fun getTextWithEndPosition(text: CharSequence, width: Float, line: Int, measuringPaint: TextPaint): Pair<List<String>, Int> {
        val result: ArrayList<String> = ArrayList()
        val sources = text.split("\n").toTypedArray()
        var position = -2

        getSize@ for (chunk in sources) {
            position += 2   // \n만큼 추가

            if (measuringPaint.measureText(chunk) < width) {
                result.add(chunk)
                position += chunk.length

                if (result.size == line) break
            } else {
                val splitChunk = splitIntoStringsThatFit(chunk, width, measuringPaint)
                for (chunkChunk in splitChunk) {
                    result.add(chunkChunk)
                    position += chunkChunk.length
                    if (result.size == line) break@getSize
                }
            }
        }

        return result to position
    }

    //https://stackoverflow.com/questions/15679147/how-to-get-line-count-of-textview-before-rendering/28525249/ 참고
    private fun splitWordsIntoStringsThatFit(source: String, maxWidthPx: Float, paint: Paint): List<String>? {
        val result: ArrayList<String> = ArrayList()
        val currentLine: ArrayList<String> = ArrayList()
        val sources = source.split("\n").toTypedArray()

        for (chunk in sources) {
            if (paint.measureText(chunk) < maxWidthPx)
                processFitChunk(maxWidthPx, paint, result, currentLine, chunk)
            else {
                val splitChunk = splitIntoStringsThatFit(chunk, maxWidthPx, paint)
                for (chunkChunk in splitChunk) {
                    processFitChunk(maxWidthPx, paint, result, currentLine, chunkChunk)
                }
            }
        }
        if (currentLine.isNotEmpty()) {
            result.add(TextUtils.join(" ", currentLine))
        }
        return result
    }

    private fun splitIntoStringsThatFit(source: String, maxWidthPx: Float, paint: Paint): List<String> {
        if (TextUtils.isEmpty(source) || paint.measureText(source) <= maxWidthPx)
            return listOf(source)

        val result: ArrayList<String> = ArrayList()
        var start = 0
        for (i in 1..source.length) {
            val subStr = source.substring(start, i)
            if (paint.measureText(subStr) >= maxWidthPx) {
                val fits = source.substring(start, i - 1)
                result.add(fits)
                start = i - 1
            }
            if (i == source.length) {
                val fits = source.substring(start, i)
                result.add(fits)
            }
        }
        return result
    }

    private fun processFitChunk(maxWidth: Float, paint: Paint, result: ArrayList<String>, currentLine: ArrayList<String>, chunk: String) {
        currentLine.add(chunk)
        val currentLineStr = TextUtils.join(" ", currentLine)
        if (paint.measureText(currentLineStr) >= maxWidth) {
            currentLine.removeAt(currentLine.size - 1)
            result.addAll(currentLine)
            currentLine.clear()
            currentLine.add(chunk)
        }
    }
}