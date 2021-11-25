package com.example.presentation.fragment.openGl

import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import androidx.fragment.app.Fragment
import com.example.presentation.R
import kotlinx.android.synthetic.main.fragment_pentagon.*
import timber.log.Timber
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class OpenGlFragment : Fragment(), SurfaceHolder.Callback, Choreographer.FrameCallback {
    private var mRenderThread: RenderThread? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pentagon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        surfaceView.holder.addCallback(this)
    }

    override fun onPause() {
        super.onPause()
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun onResume() {
        super.onResume()

        // If we already have a Surface, we just need to resume the frame notifications.
        if (mRenderThread != null) {
            Timber.d("onResume re-hooking choreographer")
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mRenderThread = RenderThread(surfaceView.holder)
        mRenderThread!!.name = "HardwareScaler GL render"
        mRenderThread!!.start()
        mRenderThread!!.waitUntilReady()
        mRenderThread?.getHandler()?.sendSurfaceCreated()

        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Timber.d("surfaceChanged format = $format  size= $width x $height  holder=$holder")

        val rh = mRenderThread!!.getHandler()
        rh?.sendSurfaceChanged(format, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Timber.d("surfaceDestroyed holder=$holder")
        // We need to wait for the render thread to shut down before continuing because we
        // don't want the Surface to disappear out from under it mid-render.  The frame
        // notifications will have been stopped back in onPause(), but there might have
        // been one in progress.
        val rh = mRenderThread!!.getHandler()
        if (rh != null) {
            rh.sendShutdown()
            try {
                mRenderThread!!.join()
            } catch (ie: InterruptedException) {
                // not expected
                throw RuntimeException("join was interrupted", ie)
            }
        }
        mRenderThread = null

        Timber.d("surfaceDestroyed complete")
    }

    override fun doFrame(frameTimeNanos: Long) {
        val rh = mRenderThread!!.getHandler()
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this)
            rh.sendDoFrame(frameTimeNanos)
        }
    }

    class RenderThread(private val surfaceHolder: SurfaceHolder) : Thread() {
        private val mDisplayProjectionMatrix = FloatArray(16)

        private var mFlatProgram: FlatShadedProgram? = null
        private var mFineTexture: Int = 0

        private val mPenta: Sprite2d = Sprite2d(Drawable2d(Drawable2d.Prefab.PENTAGON))
        private var mWindowSurface: WindowSurface? = null

        @Volatile
        private var mHandler: RenderHandler? = null

        private var mEglCore: EglCore? = null
        private val lock = ReentrantLock()
        private val condition = lock.newCondition()
        private var mReady = false

        override fun run() {
            Looper.prepare()
            mHandler = RenderHandler(this, Looper.myLooper()!!)
            mEglCore = EglCore(null, 0)

            lock.withLock {
                mReady = true
                condition.signal() // signal waitUntilReady()
            }

            Looper.loop()

            Timber.d("looper quit")
            releaseGl()
            mEglCore!!.release()

            lock.withLock { mReady = false }
        }

        fun getHandler(): RenderHandler? {
            return mHandler
        }

        fun surfaceCreated() {
            val surface: Surface = surfaceHolder.surface
            prepareGl(surface)
        }

        fun surfaceChanged(width: Int, height: Int) {
            Timber.d("surfaceChanged " + width + "x" + height)

            // Use full window.
            GLES20.glViewport(0, 0, width, height)

            // Simple orthographic projection, with (0,0) in lower-left corner.
            Matrix.orthoM(mDisplayProjectionMatrix, 0, 0f, width.toFloat(), 0f, height.toFloat(), -1f, 1f)

            val smallDim = width.coerceAtMost(height)

            // Set initial shape size / position / velocity based on window size.  Movement
            // has the same "feel" on all devices, but the actual path will vary depending
            // on the screen proportions.  We do it here, rather than defining fixed values
            // and tweaking the projection matrix, so that our squares are square.
            mPenta.setColor(0.1f, 0.9f, 0.1f)
            mPenta.setTexture(mFineTexture)
            mPenta.setScale(smallDim / 3.0f, smallDim / 3.0f)
            mPenta.setPosition(width / 2.0f, height / 2.0f)

            Timber.d("mPenta: $mPenta")
        }

        fun doFrame(timeStampNanos: Long) {
            //Log.d(TAG, "doFrame " + timeStampNanos);
            update(timeStampNanos)

            val diff: Long = (System.nanoTime() - timeStampNanos) / 1000000
            if (diff > 15) {
                // too much, drop a frame
                Timber.d("diff is $diff, skipping render")
                return
            }

            draw()
            mWindowSurface!!.swapBuffers()
        }

        fun shutdown() {
            Looper.myLooper()!!.quit()
        }

        private fun prepareGl(surface: Surface) {
            Timber.d("prepareGl")
            mWindowSurface = WindowSurface(mEglCore!!, surface, false)
            mWindowSurface!!.makeCurrent()

            // Programs used for drawing onto the screen.
            mFlatProgram = FlatShadedProgram()
            mFineTexture = GeneratedTexture.createTestTexture(GeneratedTexture.Image.FINE)

            // Set the background color.
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

            // Disable depth testing -- we're 2D only.
            GLES20.glDisable(GLES20.GL_DEPTH_TEST)

            // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
            // make sure we're defining our shapes correctly.)
            GLES20.glDisable(GLES20.GL_CULL_FACE)
        }

        private fun releaseGl() {
            GlUtil.checkGlError("releaseGl start")
            if (mWindowSurface != null) {
                mWindowSurface!!.release()
                mWindowSurface = null
            }
            if (mFlatProgram != null) {
                mFlatProgram!!.release()
                mFlatProgram = null
            }
            GlUtil.checkGlError("releaseGl done")
            mEglCore!!.makeNothingCurrent()
        }

        private fun update(timeStampNanos: Long) {
        }

        private fun draw() {
            // Clear to a non-black color to make the content easily differentiable from
            // the pillar-/letter-boxing.
            GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            // Textures may include alpha, so turn blending on.
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

            mPenta.draw(mFlatProgram!!, mDisplayProjectionMatrix)

            GLES20.glDisable(GLES20.GL_BLEND)
        }

        fun waitUntilReady() {
            lock.withLock {
                while (!mReady) {
                    try {
                        condition.await()
                    } catch (ie: InterruptedException) { /* not expected */

                    }
                }
            }
        }

        init {

        }
    }

    class RenderHandler(rt: RenderThread, looper: Looper) : Handler(looper) {
        // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
        // but no real harm in it.
        private val mWeakRenderThread: WeakReference<RenderThread> = WeakReference(rt)

        fun sendSurfaceCreated() {
            sendMessage(obtainMessage(MSG_SURFACE_CREATED))
        }

        fun sendSurfaceChanged(
            format: Int, width: Int,
            height: Int
        ) {
            // ignore format
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height))
        }

        fun sendDoFrame(frameTimeNanos: Long) {
            sendMessage(
                obtainMessage(
                    MSG_DO_FRAME,
                    (frameTimeNanos shr 32).toInt(), frameTimeNanos.toInt()
                )
            )
        }

        fun sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN))
        }

        // runs on RenderThread
        override fun handleMessage(msg: Message) {
            val what = msg.what
            //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);
            val renderThread = mWeakRenderThread.get()
            if (renderThread == null) {
                Timber.d("RenderHandler.handleMessage: weak ref is null")
                return
            }

            when (what) {
                MSG_SURFACE_CREATED -> renderThread.surfaceCreated()
                MSG_SURFACE_CHANGED -> renderThread.surfaceChanged(msg.arg1, msg.arg2)
                MSG_DO_FRAME -> {
                    val timestamp = msg.arg1.toLong() shl 32 or
                            (msg.arg2.toLong() and 0xffffffffL)
                    renderThread.doFrame(timestamp)
                }
                MSG_SHUTDOWN -> renderThread.shutdown()
                else -> throw RuntimeException("unknown message $what")
            }
        }

        companion object {
            private const val MSG_SURFACE_CREATED = 0
            private const val MSG_SURFACE_CHANGED = 1
            private const val MSG_DO_FRAME = 2
            private const val MSG_SHUTDOWN = 5
        }
    }
}