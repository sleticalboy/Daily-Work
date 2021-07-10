package com.example.camera.util;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;


/**
 * Created by renlei
 * DATE: 15-11-10
 * Time: 下午4:49
 * Email: renlei0109@yeah.net
 */
public class GoogleDetectListenerImpl implements Camera.FaceDetectionListener {

    private final Handler mHandler;///用于向主线程发送信息

    public GoogleDetectListenerImpl(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = CameraInstance.RECEIVE_FACE_MSG;
            msg.obj = faces;
            msg.sendToTarget();
        }
    }
}
