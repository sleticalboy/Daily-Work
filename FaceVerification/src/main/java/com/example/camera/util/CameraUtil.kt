package com.example.camera.util

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.Camera
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.widget.ImageView

import com.example.camera.R

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午4:57
 * Email: lei.ren@renren-inc.com
 */
class CameraUtil {

    var mCamera: Camera? = null
        private set
    private var mIsPreview: Boolean = false
    // 默认开启后置摄像头
    var cameraId = Camera.CameraInfo.CAMERA_FACING_BACK
        private set
    val cameraInfo = Camera.CameraInfo()

    // get 方法
    val cameraParameters: Camera.Parameters?
        get() = if (mCamera != null) {
            mCamera!!.parameters
        } else null

    /**
     * 打开相机
     *
     * @param cameraId
     */
    fun doOpenCamera(cameraId: Int) {
        try {
            this.cameraId = cameraId
            mCamera = Camera.open(cameraId)
            // 这里的mCameraInfo必须是new出来的，不能是个null
            Camera.getCameraInfo(cameraId, cameraInfo)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 开启预览
     *
     * @param holder
     */
    fun doStartPreview(holder: SurfaceHolder) {
        if (mIsPreview) {
            mCamera!!.stopPreview()
        }
        if (mCamera != null) {
            val params = mCamera!!.parameters
            // 设置照片拍摄后的保存格式
            params.pictureFormat = PixelFormat.JPEG
            mCamera!!.setDisplayOrientation(90)// 否则方向会有问题
            // 前置与后置的不一样，这里暂时只设置前置的，后置的可以相应的去设置
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                params.flashMode = Camera.Parameters.FLASH_MODE_OFF
            }
            printSupportPreviewSize(params)
            printSupportPictureSize(params)
            printSupportFocusMode(params)
            // params.setPictureSize(params.getPreviewSize().width, params.getPictureSize().height);

            //设置的这两个size必须时支持的size大小，否则是不可以的，会出现 setParameters 错误
            val supportPreviewSize = params.supportedPreviewSizes[0]
            params.setPreviewSize(supportPreviewSize.width, supportPreviewSize.height)

            val supportPictureSize = params.supportedPictureSizes[0]
            params.setPictureSize(supportPictureSize.width, supportPictureSize.height)

            mCamera!!.parameters = params

            val parameters = mCamera!!.parameters
            Log.i(TAG, "最终设置:PreviewSize--With = " + parameters.previewSize.width
                    + "Height = " + parameters.previewSize.height)
            Log.i(TAG, "最终设置:PictureSize--With = " + parameters.pictureSize.width
                    + "Height = " + parameters.pictureSize.height)
            try {
                mCamera!!.setPreviewDisplay(holder)
                mCamera!!.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mIsPreview = true
        }
    }

    /**
     * 结束预览
     */
    fun doStopPreview() {
        if (mIsPreview) {
            mIsPreview = false
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    /**
     * 拍照
     */
    fun doTakePic() {
        if (mIsPreview && mCamera != null) {
            mCamera!!.takePicture(ShutCallBackImpl(), null, PictureCallbackImpl())
        }
    }

    /**
     * 拍照时的动作
     * 默认会有咔嚓一声
     */
    private class ShutCallBackImpl : Camera.ShutterCallback {
        override fun onShutter() {}
    }

    /**
     * 拍照后的最主要的返回
     */
    private inner class PictureCallbackImpl : Camera.PictureCallback {
        override fun onPictureTaken(data: ByteArray, camera: Camera) {
            mIsPreview = false
            Thread(Runnable {
                val filePath = ImageUtil.saveImagePath
                val file = File(filePath)
                var fos: FileOutputStream?
                try {
                    fos = FileOutputStream(file, true)
                    fos.write(data)
                    ImageUtil.saveImage(file, data, filePath)
                    fos.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
            //重新开启预览 ，不然不能继续拍照
            mCamera!!.startPreview()
            mIsPreview = true
        }
    }


    /**
     * 打印支持的previewSizes
     *
     * @param params
     */
    private fun printSupportPreviewSize(params: Camera.Parameters) {
        val previewSizes = params.supportedPreviewSizes
//        for (i in previewSizes.indices) {
//            val size = previewSizes[i]
//            Log.i("camerautil", "previewSizes:width = " + size.width + " height = " + size.height)
//        }
        previewSizes.indices
                .map { previewSizes[it] }
                .forEach {
                    Log.i("camerautil", "previewSizes:width = " + it.width + " height = " + it.height)
                }
    }

    /**
     * 打印支持的pictureSizes
     *
     * @param params
     */
    private fun printSupportPictureSize(params: Camera.Parameters) {
        val pictureSizes = params.supportedPictureSizes
        pictureSizes.indices
                .map { pictureSizes[it] }
                .forEach {
                    Log.i("camerautil", "pictureSizes:width = " + it.width
                            + " height = " + it.height)
                }
    }

    /**
     * 点击聚焦
     *
     * @param autoFocusCallback
     * @return
     */
    fun autoFocus(autoFocusCallback: Camera.AutoFocusCallback?): Boolean {
        val parameters = mCamera!!.parameters
        val supportModes = parameters.supportedFocusModes
        if (supportModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            val focusMode = parameters.focusMode
            if (Camera.Parameters.FOCUS_MODE_AUTO != focusMode) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                mCamera!!.parameters = parameters
            }
            if (autoFocusCallback != null) {
                mCamera!!.autoFocus(autoFocusCallback)
            }
            return true
        }
        return false
    }

    /**
     * 设置聚焦的区域
     *
     * @param context
     * @param event
     */
    fun setFocusArea(context: Context, event: MotionEvent) {
        if (!CameraUtil.isSupportFocusArea || mCamera == null) {
            return
        }
        var ax = (2000f * event.rawX / context.resources.displayMetrics.widthPixels - 1000).toInt()
        var ay = (2000f * event.rawY / context.resources.displayMetrics.heightPixels - 1000).toInt()

        //        int rawx = (int) event.getRawX();
        //        int rawy = (int) event.getRawY();
        //        Log.d(TAG, "widthpix" + context.getResources().getDisplayMetrics().widthPixels
        //                + "heightpix" + context.getResources().getDisplayMetrics().heightPixels);
        //        Log.d(TAG, "rawx" + rawx + "rawy" + rawy);

        //防止超出1000 ，-1000的范围
        if (ay > 900) {
            ay = 900
        } else if (ay < -900) {
            ay = -900
        }

        if (ax < -900) {
            ax = -900
        } else if (ax > 900) {
            ax = 900
        }
        //        Log.d(TAG, "ax" + ax + "ay" + ay);

        val area = Camera.Area(
                Rect(ax - 100, ay - 100, ax + 100, ay + 100), 1000)
        val areas = ArrayList<Camera.Area>()
        areas.add(area)
        val parameters = mCamera!!.parameters
        parameters.focusAreas = areas
        parameters.meteringAreas = areas
        mCamera!!.parameters = parameters
    }

    /**
     * 设置闪光灯的模式
     *
     * @param imageView
     */
    fun setFlashMode(imageView: ImageView) {
        val parameters = mCamera!!.parameters
        val flashMode = parameters.flashMode
        if (flashMode != null) {
            when (flashMode) {
                Camera.Parameters.FLASH_MODE_OFF -> {
                    imageView.setImageResource(R.drawable.camera_setting_flash_on_normal)
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_ON
                }
                Camera.Parameters.FLASH_MODE_ON -> {
                    imageView.setImageResource(R.drawable.camera_setting_flash_auto_normal)
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                }
                Camera.Parameters.FLASH_MODE_AUTO -> {
                    imageView.setImageResource(R.drawable.camera_setting_flash_off_normal)
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                }
                else -> {
                    imageView.setImageResource(R.drawable.camera_setting_flash_off_normal)
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                }
            }
            mCamera!!.parameters = parameters
        }
    }

    /**
     * 打印支持的聚焦模式
     *
     * @param params
     */
    private fun printSupportFocusMode(params: Camera.Parameters) {
        val focusModes = params.supportedFocusModes
        for (mode in focusModes) {
            Log.i("CameraUtil", "focusModes--" + mode)
        }
    }

    // 构建单例模式
    companion object {

        private val TAG = "CameraUtil"

        val PREVIEW_HAS_STARTED = 110
        val RECEIVE_FACE_MSG = 111
        private val INSTANCE = CameraUtil()

        val instance: CameraUtil
            get() {
                synchronized(INSTANCE) {
                    return INSTANCE
                }
            }

        /**
         * 是否符合设置对焦区域的SDK版本
         *
         * @return
         */
        val isSupportFocusArea: Boolean
            get() = Build.VERSION.SDK_INT >= 14
    }
}
