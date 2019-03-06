package com.sleticalboy.dailywork.util

import android.hardware.Camera
import android.os.Handler

/**
 * Created on 18-2-26.
 *
 * @author leebin
 * @version 1.0
 * @description
 */
class FaceDetectionListenerImpl(private val mHandler: Handler) : Camera.FaceDetectionListener {

    override fun onFaceDetection(faces: Array<Camera.Face>?, camera: Camera) {
        if (faces != null) {
            val msg = mHandler.obtainMessage()
            msg.what = CameraUtils.MSG_SCAN_FACE
            msg.obj = faces
            msg.sendToTarget() // send message to mHandler
        }
    }
}
