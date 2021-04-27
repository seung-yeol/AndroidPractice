package com.example.presentation.fragment.secreencapture

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.presentation.R
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class ScreenshotService : Service() {
    companion object {
        private const val TAG = "ScreenCaptureService"
        private const val DIALOG_CLOSED_DELAY_MS = 250L

        const val SCREENSHOT_ACTION = "SCREENSHOT_ACTION"
        const val SCREENSHOT_START = "SCREENSHOT_START"
        const val SCREENSHOT_STOP = "SCREENSHOT_STOP"
        const val SCREENSHOT_RESULT_CODE = "SCREENSHOT_RESULT_CODE"
        const val SCREENSHOT_INTENT = "SCREENSHOT_INTENT"

        const val SCREENSHOT_NOTIFICATION_ID = 20190806
        const val SCREENSHOT_NOTIFICATION_CHANNEL_ID = "GRIP_SCREENSHOT_NOTIFICATION_CHANNEL"
        const val SCREENSHOT_NOTIFICATION_NAME = "GRIP_SCREENSHOT"

        const val SCREENSHOT_RESULT_FILTER = "SCREENSHOT_RESULT_FILTER"
        const val IS_SCREENSHOT_SUCCESS = "IS_SCREENSHOT_SUCCESS"

        fun getStartIntent(context: Context, resultCode: Int, intent: Intent?): Intent {
            return Intent(context, ScreenshotService::class.java).apply {
                putExtra(SCREENSHOT_ACTION, SCREENSHOT_START)
                putExtra(SCREENSHOT_RESULT_CODE, resultCode)
                putExtra(SCREENSHOT_INTENT, intent)
            }
        }

        fun getStopIntent(context: Context): Intent {
            return Intent(context, ScreenshotService::class.java).apply {
                putExtra(SCREENSHOT_ACTION, SCREENSHOT_STOP)
            }
        }
    }

    private val mediaProjectionManager by lazy { getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val disposables = CompositeDisposable()
    private lateinit var screenshotThread: HandlerThread
    private lateinit var mHandler: Handler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        screenshotThread = HandlerThread("ScreenshotThread").apply { start() }
        mHandler = Handler(screenshotThread.looper)
    }

    override fun onDestroy() {
        super.onDestroy()
        screenshotThread.quit()
        disposables.dispose()
    }

    private fun isStartCommand(intent: Intent) = intent.hasExtra(SCREENSHOT_RESULT_CODE) && intent.hasExtra(SCREENSHOT_INTENT)
            && intent.hasExtra(SCREENSHOT_ACTION) && intent.getStringExtra(SCREENSHOT_ACTION) == SCREENSHOT_START

    private fun isStopCommand(intent: Intent) = intent.hasExtra(SCREENSHOT_ACTION) && intent.getStringExtra(SCREENSHOT_ACTION) == SCREENSHOT_STOP

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        fun readyProjection() {
            val resultCode = intent.getIntExtra(SCREENSHOT_RESULT_CODE, Activity.RESULT_CANCELED)
            val data: Intent = intent.getParcelableExtra(SCREENSHOT_INTENT)!!

            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data).apply {
                registerCallback(mediaProjectionCallback, mHandler)
            }
        }

        when {
            isStartCommand(intent) -> {
                startForeground()
                readyProjection()
                startScreenshot()
            }
            isStopCommand(intent) -> {
                stopService()
            }
            else -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun startForeground() {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        initNotificationChannel(notificationManager)

        val notification = createNotification()
        notificationManager.notify(SCREENSHOT_NOTIFICATION_ID, notification)
        startForeground(SCREENSHOT_NOTIFICATION_ID, notification)
    }

    private fun initNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                SCREENSHOT_NOTIFICATION_CHANNEL_ID,
                SCREENSHOT_NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }.also {
                notificationManager.createNotificationChannel(it)
            }
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, SCREENSHOT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.img_santa_1)
            .setContentTitle("Grip")
            .setContentText("스크린샷을 찍는 중 입니다.")
            .setShowWhen(true)
            .build()
    }

    private val mediaProjectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            virtualDisplay?.release()
            imageReader?.setOnImageAvailableListener(null, null)
            mediaProjection?.unregisterCallback(this)
        }
    }

    private fun stopProjection() {
        mediaProjection?.stop()
    }

    private fun startScreenshot() {
        mHandler.postDelayed({ screenshot() }, DIALOG_CLOSED_DELAY_MS)
    }

    private fun screenshot() {
        singleScreenImageReader()
            .getScreenImage()
            .createBitmap()
            .saveBitmap()
            .doFinally { stopService() }
            .subscribe({
                Intent(SCREENSHOT_RESULT_FILTER)
                    .apply { putExtra(IS_SCREENSHOT_SUCCESS, true) }
                    .also { LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(it) }
            }, { error ->
                Timber.tag(TAG).e(error)
                Intent(SCREENSHOT_RESULT_FILTER)
                    .apply { putExtra(IS_SCREENSHOT_SUCCESS, false) }
                    .also { LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(it) }
            }).addTo(disposables)
    }

    private fun stopService() {
        stopProjection()
        stopSelf()
    }

    @SuppressLint("WrongConstant")
    private fun singleScreenImageReader(): Single<ImageReader> {
        return Single.create { emitter ->
            val metrics = resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)

            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "displayForCapture", width, height, metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader!!.surface, null, mHandler
            )

            imageReader!!.setOnImageAvailableListener({ imageReader ->
                if (imageReader != null) emitter.onSuccess(imageReader)
                else emitter.onError(Throwable("가져올 이미지가 없습니다."))
            }, mHandler)

            if (imageReader == null || virtualDisplay == null) emitter.onError(Throwable("가져올 이미지가 없습니다."))
        }
    }

    private fun Single<ImageReader>.getScreenImage(): Single<Image> {
        return map { it.acquireLatestImage() }
    }

    private fun Single<Image>.createBitmap(): Single<Bitmap> {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        return map {
            val planes: Array<Image.Plane> = it.planes
            val pixelStride: Int = planes[0].pixelStride
            val rowStride: Int = planes[0].rowStride
            val rowPadding: Int = rowStride - pixelStride * width
            val buffer: ByteBuffer = planes[0].buffer

            Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888).apply { copyPixelsFromBuffer(buffer) }
        }
    }

    private fun Single<Bitmap>.saveBitmap(): Completable {
        return flatMapCompletable { bitmap ->
            val uri = createContentUri()

            if (uri == null) {
                Completable.error(Exception("이미지의 uri가 존재하지 앖습니다."))
            } else {
                applicationContext.contentResolver.openFileDescriptor(uri, "rw")
                    .use {
                        Single.just(
                            FileOutputStream(it!!.fileDescriptor).use { fos -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) }
                        )
                    }.flatMapCompletable {
                        if (it) Completable.complete()
                        else Completable.error(Exception("bitmap 저장을 실패하였습니다."))
                    }.doOnError { bitmap.recycle() }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createContentUri(): Uri? {
        val time = SimpleDateFormat("yyyyMMdd-HHmmss").format(Date())

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.DISPLAY_NAME, "${time}.jpg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Grip")
            }.let { applicationContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it) }
        } else {
            @Suppress("DEPRECATION")
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath, "/Grip")
                .also { it.mkdirs() }
                .let { Uri.fromFile(File(it.absolutePath, "$time.jpg")) }
        }
    }
}