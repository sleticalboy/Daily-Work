package com.example.camera

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
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
import com.example.camera.preview.FaceView
import com.example.camera.preview.MySurfacePreview
import com.example.camera.util.CameraUtil
import com.example.camera.util.GoogleDetectListenerImpl
import java.lang.ref.WeakReference

class CameraActivity : Activity() {

    private var mySurfacePreview: MySurfacePreview? = null
    private var takeBtn: ImageButton? = null
    private var focusLayout: FrameLayout? = null
    private var changeFlashModeIV: ImageView? = null
    private var switchCameraIV: ImageView? = null
    private var settingRl: RelativeLayout? = null
    private var faceView: FaceView? = null
    private var width: Int = 0
    private var height: Int = 0

    private val gestureDetector = GestureDetector(object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Log.d("MyGestureDetector", "onSingleTapUp")
            CameraUtil.instance.autoFocus(Camera.AutoFocusCallback { success, camera ->
                if (success) {
                    Log.d("renlei", "聚焦成功")
                } else {
                    Log.d("renlei", "聚焦失败")
                }
                focusLayout!!.visibility = View.GONE
            })
            CameraUtil.instance.setFocusArea(this@CameraActivity, e)
            showFocusIcon(e)
            return true
        }
    })

    private val mainHandler = MainHandler(this)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        initView()
        bindListener()
    }

    private fun initView() {
        mySurfacePreview = findViewById<View>(R.id.my_surfaceview) as MySurfacePreview
        mySurfacePreview!!.handler = mainHandler
        takeBtn = findViewById<View>(R.id.take_btn) as ImageButton
        changeFlashModeIV = findViewById<View>(R.id.flash_iv) as ImageView
        switchCameraIV = findViewById<View>(R.id.switch_camera_iv) as ImageView
        settingRl = findViewById<View>(R.id.setting_rl) as RelativeLayout
        faceView = findViewById<View>(R.id.face_view) as FaceView
        focusLayout = findViewById<View>(R.id.camera_focus_layout) as FrameLayout
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        focusLayout!!.measure(w, h)
        width = focusLayout!!.measuredWidth / 2
        height = focusLayout!!.measuredHeight / 2
        Log.d("CameraActivity", "width:" + width)
        Log.d("CameraActivity", "height:" + height)
    }

    private fun bindListener() {
        takeBtn!!.setOnClickListener(TakeBtnClickListener())

        mySurfacePreview!!.setOnTouchListener { _, event ->
            if (CameraUtil.instance.cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                gestureDetector.onTouchEvent(event)
            } else {
                false
            }
        }

        changeFlashModeIV!!.setOnClickListener { CameraUtil.instance.setFlashMode(changeFlashModeIV!!) }

        switchCameraIV!!.setOnClickListener { changeCamera() }
    }

    private inner class TakeBtnClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            CameraUtil.instance.doTakePic()
        }
    }

    private fun showFocusIcon(e: MotionEvent) {
        val x = e.x.toInt()
        val y = e.y.toInt()
        val params = focusLayout!!.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = (x - width + 0.5).toInt()
        params.topMargin = ((y - height).toDouble() + 0.5 + settingRl!!.height.toDouble()).toInt()
        //        focusLayout.setLayoutParams(params); // 等价于 focusLayout.requestLayout();
        focusLayout!!.requestLayout()
        focusLayout!!.visibility = View.VISIBLE
    }

    fun changeCamera() {
        CameraUtil.instance.doStopPreview()
        val newCameraId = (CameraUtil.instance.cameraId + 1) % 2
        CameraUtil.instance.doOpenCamera(newCameraId)
        CameraUtil.instance.doStartPreview(mySurfacePreview!!.holder)
        if (newCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchCameraIV!!.setImageResource(R.drawable.camera_setting_switch_back)
            changeFlashModeIV!!.visibility = View.VISIBLE
        } else {
            switchCameraIV!!.setImageResource(R.drawable.camera_setting_switch_front)
            changeFlashModeIV!!.visibility = View.GONE
        }
    }

    private fun startGoogleDetect() {
        val parameters = CameraUtil.instance.cameraParameters
        val camera = CameraUtil.instance.mCamera
        if (parameters!!.maxNumDetectedFaces > 0) {
            if (faceView != null) {
                faceView!!.clearFaces()
                faceView!!.visibility = View.VISIBLE
            }
            camera!!.setFaceDetectionListener(GoogleDetectListenerImpl(mainHandler))
            camera.startFaceDetection()
        }
    }

    private class MainHandler internal constructor(activity: Activity) : Handler() {

        internal var mActivity: WeakReference<Activity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get() as CameraActivity
            val what = msg.what
            when (what) {
                CameraUtil.PREVIEW_HAS_STARTED -> {
                    Log.e("tag", "开启人脸识别")
                    activity.startGoogleDetect()
                }
                CameraUtil.RECEIVE_FACE_MSG -> activity.runOnUiThread {
                    Log.e("tag", "收到人脸识别的信息")
                    val faces = msg.obj as Array<Camera.Face>
                    activity.faceView!!.setFaces(faces)
                }
            }
        }
    }
}
