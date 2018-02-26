package com.example.camera.util

import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午7:21
 * Email: lei.ren@renren-inc.com
 */
object ImageUtil { // 工具类：所有的方法均为 static 修饰

    // get 方法
    val saveImagePath: String
        get() {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path = (Environment.getExternalStorageDirectory().path + "/renlei/"
                        + System.currentTimeMillis() + ".jpg")
                val file = File(path)
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                return path
            }
            return System.currentTimeMillis().toString() + ".jpg"
        }

    fun saveImage(file: File, data: ByteArray, filePath: String) {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val tempBitmap = BitmapFactory.decodeFile(filePath, options)
        val degrees = getExifRotateDegree(filePath)
    }

    private fun getExifRotateDegree(path: String): Int {
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val degrees = getExifRotateDegrees(orientation)
            Log.d("imageutil degrees", degrees.toString() + "")
            return degrees
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return 0
    }

    private fun getExifRotateDegrees(exifOrientation: Int): Int {
        var degrees = 0
        when (exifOrientation) { // switch 语句
            ExifInterface.ORIENTATION_NORMAL -> degrees = 0
            ExifInterface.ORIENTATION_ROTATE_90 -> degrees = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degrees = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degrees = 270
        }
        return degrees
    }
}
