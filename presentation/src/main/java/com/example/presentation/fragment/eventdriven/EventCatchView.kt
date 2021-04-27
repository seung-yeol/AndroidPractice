package com.example.presentation.fragment.eventdriven

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.UnitTransformation
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.presentation.R
import io.reactivex.Completable
import java.lang.Math.abs
import kotlin.random.Random

class EventCatchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val animationGenerators: List<AnimationGenerator> by lazy { createAnimations() }
    private val eventDrawables: MutableList<Drawable> = mutableListOf()
    private val eventTargetView: ImageView = ImageView(context).also {
        minimumHeight = 360
        it.maxWidth = 360
        it.maxHeight = 360
        addView(it, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
    private val resultView: ImageView = ImageView(context)
        .apply { visibility = GONE }
        .also {

            addView(it, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            it.updateLayoutParams<LayoutParams> { gravity = Gravity.CENTER }
        }

    private var currentAnimator: AnimatorSet? = null

    data class AnimationGenerator(val growingType: GrowingType, val generate: (x: Float, y: Float) -> AnimatorSet)

    data class Position(val x: Float, val y: Float)

    enum class GrowingType { NORMAL, BIG }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        Glide.with(context)
            .load(R.raw.damn2)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(UnitTransformation.get()))
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    (resource as? WebpDrawable)?.apply { loopCount = 1 }

                    return false
                }
            })
            .into(resultView)
    }

    private fun animateEventTargetGone() {
        ObjectAnimator.ofPropertyValuesHolder(eventTargetView, PropertyValuesHolder.ofFloat(View.ALPHA, 0f)).apply { this.duration = 300 }.start()
    }

    private fun createAnimations(): List<AnimationGenerator> {
        fun visibleChange(start: Float, end: Float, duration: Long) = ObjectAnimator.ofPropertyValuesHolder(
            eventTargetView,
            PropertyValuesHolder.ofFloat(View.ALPHA, start, end)
        ).apply { this.duration = duration }

        fun scaleChange(start: Float, end: Float, duration: Long) = ObjectAnimator.ofPropertyValuesHolder(
            eventTargetView,
            PropertyValuesHolder.ofFloat(SCALE_X, start, end),
            PropertyValuesHolder.ofFloat(SCALE_Y, start, end)
        ).apply { this.duration = duration }

        fun fadeInOutWith(scalingRate: Float, duration: Long, vararg animators: Animator): AnimatorSet {
            return AnimatorSet().apply {
                playTogether(listOf(AnimatorSet().apply {
                    playSequentially(
                        AnimatorSet().apply { playTogether(visibleChange(0f, 1f, 200), scaleChange(0f, scalingRate, duration / 4)) },
                        AnimatorSet().apply {
                            startDelay = duration / 2
                            playTogether(visibleChange(1f, 0f, 200), scaleChange(scalingRate, 0f, duration / 4))
                        }
                    )
                }) + animators)
            }
        }

        fun move(x: Float, y: Float, duration: Long) = ObjectAnimator.ofPropertyValuesHolder(
            eventTargetView,
            PropertyValuesHolder.ofFloat(X, eventTargetView.x, x),
            PropertyValuesHolder.ofFloat(Y, eventTargetView.y, y)
        ).apply { this.duration = duration }

        fun moveEndToEndWith(isStartFromLeft: Boolean, duration: Long, vararg animators: Animator): AnimatorSet {
            eventTargetView.apply {
                scaleX = 1f
                scaleY = 1f
                alpha = 1f
                visibility = VISIBLE
            }

            val startX = (if (isStartFromLeft) paddingLeft - eventTargetView.width else width - paddingRight).toFloat()
            val endX = (if (isStartFromLeft) width - paddingRight else -eventTargetView.width + paddingLeft).toFloat()

            return AnimatorSet().apply {
                playTogether(
                    listOf(
                        ObjectAnimator.ofPropertyValuesHolder(eventTargetView, PropertyValuesHolder.ofFloat(X, startX, endX)).apply { this.duration = duration },
                    ) + animators
                )
            }
        }

        fun rotate(startDegree: Float, endDegrees: Float, duration: Long) =
            ObjectAnimator.ofPropertyValuesHolder(eventTargetView, PropertyValuesHolder.ofFloat(View.ROTATION, startDegree, endDegrees)).apply { this.duration = duration }

        fun waddle(duration: Long): AnimatorSet {
            fun oneWaddleAnimation() = AnimatorSet().apply {
                playSequentially(
                    rotate(0f, 30f, 100),
                    rotate(30f, -30f, 200),
                    rotate(-30f, 0f, 100)
                )
            }

            return AnimatorSet().apply {
                playSequentially(MutableList((duration / 400).toInt()) { oneWaddleAnimation() }.toList())
                interpolator = AccelerateDecelerateInterpolator()
            }
        }

        //나머지는 움직이는 경우 애니메이션 시간 잘 설정해
        fun duration(from: Int, until: Int) = Random.nextInt(from, until).toLong()
        fun fixedAnimDuration() = Random.nextInt(1000, 1300).toLong()
        fun moveAnimDuration() = Random.nextInt(1200, 1800).toLong()

        fun bigScalingRate() = Random.nextInt(10, 17).toFloat() / 10
        fun degree(from: Int, until: Int) = Random.nextInt(from, until).toFloat()

        val normalScaleFadeInOut: (x: Float, y: Float) -> AnimatorSet = { _, _ ->
            println("osy normalScaleFadeInOut")
            fadeInOutWith(1f, fixedAnimDuration()).apply { doOnStart { eventTargetView.rotation = Random.nextInt(0, 360).toFloat() } }
        }
        val bigScaleFadeInOut: (x: Float, y: Float) -> AnimatorSet = { _, _ ->
            println("osy bigScaleFadeInOut")
            fadeInOutWith(bigScalingRate(), fixedAnimDuration())
        }
        val bigScaleRotate: (x: Float, y: Float) -> AnimatorSet = { _, _ ->
            println("osy bigScaleRotate")
            val degree = degree(270, 1080)
            val duration = fixedAnimDuration()
            fadeInOutWith(2f, fixedAnimDuration(), rotate(0f, degree, duration).apply { interpolator = AccelerateInterpolator(1.5f) })
                .apply {
                    doOnEnd {
                        println("osy bigScaleRotate rotation : ${eventTargetView.rotation}")
                        eventTargetView.rotation -= degree
                    }
                }
        }
        val move: (x: Float, y: Float) -> AnimatorSet = { x, y ->
            println("osy move")
            val duration = moveAnimDuration()
            fadeInOutWith(1f, duration, move(x, y, duration)).apply { doOnStart { eventTargetView.rotation = Random.nextInt(0, 360).toFloat() } }
        }
        val moveWithWaddle: (x: Float, y: Float) -> AnimatorSet = { x, y ->
            println("osy moveWithWaddle")
            val duration = moveAnimDuration()
            fadeInOutWith(1f, duration, move(x, y, duration), waddle(duration))
        }
        val moveWithRotate: (x: Float, y: Float) -> AnimatorSet = { x, y ->
            println("osy moveWithRotate")
            val degree = degree(270, 1080)
            val duration = moveAnimDuration()
            fadeInOutWith(1f, duration, move(x, y, duration), rotate(0f, degree, duration)).apply { doOnEnd { eventTargetView.rotation -= degree } }
        }
        val endToEnd: (x: Float, y: Float) -> AnimatorSet = { _, _ ->
            println("osy endToEnd")
            moveEndToEndWith(Random.nextBoolean(), duration(1400, 2000)).apply { doOnStart { eventTargetView.rotation = Random.nextInt(0, 360).toFloat() } }
        }
        val endToEndWithWaddle: (x: Float, y: Float) -> AnimatorSet = { _, _ ->
            println("osy endToEndWithWaddle")
            val duration = duration(1400, 2000)
            moveEndToEndWith(Random.nextBoolean(), duration, waddle(duration))
        }
        val endToEndWithRotate: (x: Float, y: Float) -> AnimatorSet = { _, _ ->
            println("osy endToEndWithRotate")
            val degree = degree(360, 1080)
            val isStartFromLeft = Random.nextBoolean()
            val duration = duration(1400, 2000)

            moveEndToEndWith(isStartFromLeft, duration, rotate(0f, degree * if (isStartFromLeft) 1 else -1, duration))
        }

        return listOf(
            AnimationGenerator(GrowingType.NORMAL, normalScaleFadeInOut),
            AnimationGenerator(GrowingType.BIG, bigScaleFadeInOut),
            AnimationGenerator(GrowingType.BIG, bigScaleRotate),
            AnimationGenerator(GrowingType.NORMAL, move),
            AnimationGenerator(GrowingType.NORMAL, moveWithRotate),
            AnimationGenerator(GrowingType.NORMAL, moveWithWaddle),
            AnimationGenerator(GrowingType.NORMAL, endToEnd),
            AnimationGenerator(GrowingType.NORMAL, endToEndWithWaddle),
            AnimationGenerator(GrowingType.NORMAL, endToEndWithRotate)
        )
    }

    fun setEventDrawables(drawables: List<Drawable>) {
        eventDrawables.clear()
        eventDrawables.addAll(drawables)
        eventTargetView.setImageDrawable(drawables.first())
    }

    fun setOnEventClickListener(onClick: () -> Unit) {
        eventTargetView.setOnClickListener {
            currentAnimator?.pause()
            animateEventTargetGone()
            onClick.invoke()
            showFail()
        }
    }

    fun showFail() {
        resultView.visibility = VISIBLE
        (resultView.drawable as? WebpDrawable)?.start()
    }

    fun showPrize() {

    }

    //애니메이션 종료 후, 위치 초기화 // 범위 제약 후 랜덤 위치
    fun startEvent() {
        println("osy x : $x  y : $y")
        println("osy targetWidth : ${eventTargetView.width}  targetHeight : ${eventTargetView.height}")
//        println("osy range x : ${eventTargetView.width / 2 + paddingLeft} ~ ${width - eventTargetView.width * 3 / 2 - paddingRight} |  y : ${eventTargetView.height / 2 + paddingTop} ~ ${height - eventTargetView.height * 3 / 2 - paddingBottom}")

        val animationGenerator = animationGenerators.random()

        val (startX, startY) = getStartPosition(animationGenerator.growingType)
        val (endX, endY) = getEndPosition(startX, startY)

        println("osy startX : $startX  startY : $startY  ")

        eventTargetView.apply {
            setImageDrawable(drawable)
            visibility = VISIBLE
            x = startX
            y = startY
        }

        startAnimation(endX, endY, animationGenerator)
    }

    private fun getStartPosition(growingType: GrowingType): Position {
        return when (growingType) {
            GrowingType.NORMAL -> {
                Position(
                    Random.nextInt(paddingLeft, width - eventTargetView.width - paddingRight).toFloat(),
                    Random.nextInt(paddingTop, height - eventTargetView.height - paddingBottom).toFloat()
                )
            }
            GrowingType.BIG -> {
                Position(
                    Random.nextInt(eventTargetView.width / 2 + paddingLeft, width - eventTargetView.width * 3 / 2 - paddingRight).toFloat(),
                    Random.nextInt(eventTargetView.height / 2 + paddingTop, height - eventTargetView.height * 3 / 2 - paddingBottom).toFloat()
                )
            }
        }
    }

    private fun getEndPosition(startX: Float, startY: Float): Position {
        var minDistance = 300

        val startXGenerators = mutableListOf<() -> Float>()

        fun initStartXGenerators() {
            if (startX.toInt() - minDistance >= paddingLeft)
                startXGenerators.add { Random.nextInt(paddingLeft, startX.toInt() - minDistance).toFloat() }
            if (width - eventTargetView.width >= startX + minDistance)
                startXGenerators.add { Random.nextInt(startX.toInt() + minDistance, width - eventTargetView.width).toFloat() }
        }

        do {
            println("osy minDistance : $minDistance")
            initStartXGenerators()
            minDistance -= 20
        } while (startXGenerators.isEmpty())

        val endX = startXGenerators.random().invoke()

        println("osy startX : $startX , endX : $endX  distance : ${abs(endX - startX)}")
        return Position(endX, startY)
    }

    private fun startAnimation(endX: Float, endY: Float, animationGenerator: AnimationGenerator) {
        currentAnimator = animationGenerator.generate.invoke(endX, endY).apply {
            doOnEnd {
                eventTargetView.apply {
                    alpha = 0f
                    rotation = 0f
                    visibility = GONE
                }
            }
            start()
        }
    }
}