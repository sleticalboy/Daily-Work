package com.example.camera.util;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * Created by renlei
 * DATE: 15-11-10
 * Time: 下午4:49
 * Email: renlei0109@yeah.net
 */
public class GoogleDetectListenerImpl implements Camera.FaceDetectionListener {

    private Handler mHandler;///用于向主线程发送信息
    private Context mContext;

    public GoogleDetectListenerImpl(Context mContext, Handler mHandler) {
        this.mHandler = mHandler;
        this.mContext = mContext;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces != null) {
            Message msg = mHandler.obtainMessage();
            msg.what = CameraUtil.RECEIVE_FACE_MSG;
            msg.obj = faces;
            msg.sendToTarget();
        }

    }
}
