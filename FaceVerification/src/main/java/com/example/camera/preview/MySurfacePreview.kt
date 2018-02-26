package com.example.camera.preview

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Camera
import android.os.Handler
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.camera.util.CameraUtil

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午4:52
 */
// : 表示 extends , 表示 implements
class MySurfacePreview(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    // val 表示 final
    private val mSurfaceHolder: SurfaceHolder = holder
    // ? = 对象初始化
    private var mHandler: Handler? = null

    // 在构造方法中所做的初始化工作
    init {
        // translucent半透明 transparent透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT)
        mSurfaceHolder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // 开启相机
        CameraUtil.instance.doOpenCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // 开始预览
        CameraUtil.instance.doStartPreview(mSurfaceHolder)
        if (mHandler != null) {
            // !! 表示非空对象，可以直接调用属性或者方法
            mHandler!!.postDelayed({ mHandler!!.sendEmptyMessage(CameraUtil.PREVIEW_HAS_STARTED) }, 1000)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        // 停止预览
        CameraUtil.instance.doStopPreview()
    }

    fun setHandler(handler: Handler) {
        mHandler = handler
    }
}
