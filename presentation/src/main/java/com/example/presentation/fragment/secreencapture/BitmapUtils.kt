package com.example.presentation.fragment.secreencapture

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {
    fun centerCrop(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val parentRatio = bitmap.width / bitmap.height.toFloat()
        val dialogRatio = width / height.toFloat()

        return when {
            parentRatio > dialogRatio -> {
                Bitmap.createBitmap(
                        bitmap,
                        ((bitmap.width - width * bitmap.height / height.toFloat()) / 2).toInt(),
                        0,
                        ((width * bitmap.height / height.toFloat())).toInt(),
                        bitmap.height
                )
            }
            parentRatio < dialogRatio -> {
                Bitmap.createBitmap(
                        bitmap,
                        0,
                        ((bitmap.height - height * bitmap.width / width.toFloat()) / 2).toInt(),
                        bitmap.width,
                        (height * bitmap.width / width.toFloat()).toInt()
                )
            }
            else -> bitmap
        }
    }

    fun saveBitmapToPicturesDir(context: Context, bitmap: Bitmap, fileName: String): Completable {
        val uri = createContentUri(context, fileName)

        return if (uri == null) {
            Completable.error(Exception("이미지의 uri가 존재하지 앖습니다."))
        } else {
            context.contentResolver.openFileDescriptor(uri, "rw")
                    .use {
                        Single.just(
                                FileOutputStream(it!!.fileDescriptor).use { fos -> bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos) }
                        )
                    }.flatMapCompletable {
                        if (it) Completable.complete()
                        else Completable.error(Exception("bitmap 저장을 실패하였습니다."))
                    }.doOnEvent { bitmap.recycle() }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createContentUri(context: Context, fileName: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.DISPLAY_NAME, "${fileName}.jpg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Grip")
            }.let { context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it) }
        } else {
            @Suppress("DEPRECATION")
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath, "/Grip")
                    .also { it.mkdirs() }
                    .let { Uri.fromFile(File(it.absolutePath, "$fileName.jpg")) }
        }
    }
}