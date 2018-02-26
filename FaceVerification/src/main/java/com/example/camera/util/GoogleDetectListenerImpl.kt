package com.example.camera.util

import android.hardware.Camera
import android.os.Handler
import android.os.Message

/**
 * Created by renlei
 * DATE: 15-11-10
 * Time: 下午4:49
 * Email: renlei0109@yeah.net
 */
class GoogleDetectListenerImpl(private val mHandler: Handler) : Camera.FaceDetectionListener {

    // mHandler 用于向主线程发送信息

    override fun onFaceDetection(faces: Array<Camera.Face>?, camera: Camera) {
        if (faces != null) {
            val msg = mHandler.obtainMessage()
            msg.what = CameraUtil.RECEIVE_FACE_MSG
            msg.obj = faces
            msg.sendToTarget()
        }
    }
}
