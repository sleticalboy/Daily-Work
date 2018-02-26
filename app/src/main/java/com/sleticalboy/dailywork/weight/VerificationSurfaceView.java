package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sleticalboy.dailywork.util.CameraUtils;

/**
 * Created on 18-2-24.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class VerificationSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Handler mHandler;

    public VerificationSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraUtils.getInstance().openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraUtils.getInstance().startPreview(holder);
        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(CameraUtils.MSG_PREVIEW_STARTED, 1000);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtils.getInstance().stopPreview();
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }
}
