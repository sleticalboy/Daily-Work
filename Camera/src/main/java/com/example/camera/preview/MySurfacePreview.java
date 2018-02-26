package com.example.camera.preview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.camera.util.CameraUtil;

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午4:52
 */
public class MySurfacePreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
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
        CameraUtil.getInstance().doOpenCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraUtil.getInstance().doStartPreview(surfaceHolder);
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(CameraUtil.PREVIEW_HAS_STARTED);
                }
            }, 1000);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtil.getInstance().doStopPreview();
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}
