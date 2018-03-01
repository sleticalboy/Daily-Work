package com.sleticalboy.dailywork.ui.activity

import android.app.Activity
import android.hardware.Camera
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout

import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.dailywork.util.CameraUtils
import com.sleticalboy.dailywork.util.FaceDetectionListenerImpl
import com.sleticalboy.dailywork.weight.FaceView
import com.sleticalboy.dailywork.weight.VerificationSurfaceView

import java.lang.ref.WeakReference

/**
 * Created on 18-2-24.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
class FaceActivity : BaseActivity() {

    private var btnSwitchCamera: ImageView? = null
    private var btnSwitchFlash: ImageView? = null
    private var verifySurfaceView: VerificationSurfaceView? = null
    private var mFaceView: FaceView? = null
    private var btnTakePic: ImageButton? = null
    private var flCameraFocus: FrameLayout? = null
    private var rlSettings: RelativeLayout? = null

    private val mHandler = MainHandler(this)
    private var mHeight: Int = 0
    private var mWidth: Int = 0

    private val mGestureDetector = GestureDetector(object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            Log.d("action", "down")
            return true
        }

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            Log.d("touch", "single touch")
            CameraUtils.instance.autoFocus(Camera.AutoFocusCallback { success, _ ->
                if (success) {
                    Log.d("FaceActivity", "聚焦成功")
                } else {
                    Log.d("FaceActivity", "聚焦失败")
                }
                flCameraFocus!!.visibility = View.GONE
            })
            CameraUtils.instance.setFocusArea(this@FaceActivity, event)
            showFocusLayout(event)
            return true
        }
    })

    override fun initData() {}

    override fun initView() {
        rlSettings = findViewById<View>(R.id.rlSettings) as RelativeLayout
        btnSwitchCamera = findViewById<View>(R.id.btnSwitchCamera) as ImageView?
        btnSwitchFlash = findViewById<View>(R.id.btnSwitchFlash) as ImageView?

        verifySurfaceView = findViewById<View>(R.id.verifySurfaceView) as VerificationSurfaceView?
        verifySurfaceView!!.handler = mHandler

        mFaceView = findViewById<View>(R.id.pictureView) as FaceView?
        btnSwitchCamera = findViewById<View>(R.id.btnSwitchCamera) as ImageView?
        btnTakePic = findViewById<View>(R.id.btnTakePic) as ImageButton?

        flCameraFocus = findViewById<View>(R.id.flCameraFocus) as FrameLayout?
        // width 和 height 是具体数值时可以用此种方法测量
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((1 shl 30) - 1, View.MeasureSpec.AT_MOST)
        flCameraFocus!!.measure(widthMeasureSpec, heightMeasureSpec)
        mWidth = flCameraFocus!!.measuredWidth / 2
        mHeight = flCameraFocus!!.measuredHeight / 2
        Log.d("measure", "mWidth:" + mWidth)
        Log.d("measure", "mHeight:" + mHeight)

        initListeners()
    }

    // 当 Activity 的窗口得到或者失去焦点时均会被调用一次，即当 Activity 继续执行和暂停执行时，此方法会被调用
    // 也就是说当频繁的调用 onResume 和 onPause 方法时，此方法会被频繁调用
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // visibility 属性是 invisibility 或者 gone 时测量结果为 0
            val width = flCameraFocus!!.measuredWidth / 2
            val height = flCameraFocus!!.measuredHeight / 2
            Log.d("onWindowFocusChanged", "width:" + width)
            Log.d("onWindowFocusChanged", "height:" + height)
        }
    }

    private fun initListeners() {
        btnTakePic!!.setOnClickListener { CameraUtils.instance.takePicture() }
        btnSwitchCamera!!.setOnClickListener { changeCamera() }
        btnSwitchFlash!!.setOnClickListener { v -> CameraUtils.instance.setFlashMode(v as ImageView) }

        verifySurfaceView!!.setOnTouchListener { _, event ->
            if (CameraUtils.instance.cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mGestureDetector.onTouchEvent(event)
            } else {
                false
            }
        }
    }

    private fun changeCamera() {
        CameraUtils.instance.stopPreview()
        val newCameraId = (CameraUtils.instance.cameraId + 1) % 2
        CameraUtils.instance.openCamera(newCameraId)
        CameraUtils.instance.startPreview(verifySurfaceView!!.holder)
        if (newCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            btnSwitchCamera!!.setImageResource(R.mipmap.camera_setting_switch_back)
            btnSwitchFlash!!.visibility = View.VISIBLE
        } else {
            btnSwitchCamera!!.setImageResource(R.mipmap.camera_setting_switch_front)
            btnSwitchFlash!!.visibility = View.GONE
        }
    }

    private fun startDetect() {
        val params = CameraUtils.instance.cameraParameters
        val camera = CameraUtils.instance.mCamera
        if (params!!.maxNumDetectedFaces > 0) {
            if (mFaceView != null) {
                mFaceView!!.clearFaces()
                mFaceView!!.visibility = View.GONE
            }
        }
        camera!!.setFaceDetectionListener(FaceDetectionListenerImpl(mHandler))
        camera.startFaceDetection()
    }

    override fun attachLayout(): Int {
        return R.layout.activity_face
    }

    internal class MainHandler(activity: Activity) : Handler() {

        private val mActivity: WeakReference<Activity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get() as FaceActivity
            when (msg.what) {
                CameraUtils.MSG_PREVIEW_STARTED -> activity.startDetect()
                CameraUtils.MSG_SCAN_FACE -> activity.runOnUiThread {
                    val faces = msg.obj as Array<Camera.Face>
                    activity.mFaceView!!.setFaces(faces)
                }
            }
        }
    }

    // 点击屏幕显示聚焦图标
    private fun showFocusLayout(event: MotionEvent) {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val params = flCameraFocus!!.layoutParams as FrameLayout.LayoutParams
        params.leftMargin = (x - mWidth + 0.5).toInt()
        Log.d("rlSettings'", "height = " + rlSettings!!.height)
        params.topMargin = ((y - mHeight).toDouble() + 0.5 /*+ rlSettings!!.height.toDouble()*/).toInt()
        flCameraFocus!!.layoutParams = params
        flCameraFocus!!.visibility = View.VISIBLE
    }
}
