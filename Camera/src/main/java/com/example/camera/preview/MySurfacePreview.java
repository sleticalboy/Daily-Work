package com.example.camera.preview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.camera.util.CameraInstance;

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午4:52
 */
public class MySurfacePreview extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder surfaceHolder;
    private Handler mHandler;

    public MySurfacePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        //translucent半透明 transparent透明
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraInstance.get().doOpenCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraInstance.get().doStartPreview(surfaceHolder);
        if (mHandler != null) {
            mHandler.postDelayed(() -> mHandler.sendEmptyMessage(CameraInstance.PREVIEW_HAS_STARTED), 1000);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraInstance.get().doStopPreview();
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}
