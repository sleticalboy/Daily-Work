package com.sleticalboy.dailywork.ui.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.util.CameraUtils;
import com.sleticalboy.dailywork.util.FaceDetectionListenerImpl;
import com.sleticalboy.dailywork.weight.FaceView;
import com.sleticalboy.dailywork.weight.VerificationSurfaceView;

import java.lang.ref.WeakReference;

/**
 * Created on 18-2-24.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class FaceActivity extends BaseActivity {

    ImageView btnSwitchCamera;
    ImageView btnSwitchFlash;
    VerificationSurfaceView verifySurfaceView;
    FaceView mFaceView;
    ImageButton btnTakePic;
    FrameLayout flCameraFocus;
    RelativeLayout rlSettings;

    private Handler mHandler = new MainHandler(this);
    private int mHeight;
    private int mWidth;

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        rlSettings = findViewById(R.id.rlSettings);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnSwitchFlash = findViewById(R.id.btnSwitchFlash);

        verifySurfaceView = findViewById(R.id.verifySurfaceView);
        verifySurfaceView.setHandler(mHandler);

        mFaceView = findViewById(R.id.pictureView);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnTakePic = findViewById(R.id.btnTakePic);

        flCameraFocus = findViewById(R.id.flCameraFocus);
        // width 和 height 是具体数值时可以用此种方法测量
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(120, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(120, View.MeasureSpec.EXACTLY);
        flCameraFocus.measure(widthMeasureSpec, heightMeasureSpec);
        mWidth = flCameraFocus.getMeasuredWidth();
        mHeight = flCameraFocus.getMeasuredHeight();
        Log.d("measure", "mWidth:" + mWidth);
        Log.d("measure", "mHeight:" + mHeight);

        flCameraFocus.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // visibility 属性是 invisibility 或者 gone 时测量结果为 0
                mWidth = flCameraFocus.getMeasuredWidth() / 2;
                mHeight = flCameraFocus.getMeasuredHeight() / 2;
                Log.d("onPreDraw", "mWidth:" + mWidth);
                Log.d("onPreDraw", "mHeight:" + mHeight);
                flCameraFocus.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        initListeners();
    }

    // 当 Activity 的窗口得到或者失去焦点时均会被调用一次，即当 Activity 继续执行和暂停执行时，此方法会被调用
    // 也就是说当频繁的调用 onResume 和 onPause 方法时，此方法会被频繁调用
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // visibility 属性是 invisibility 或者 gone 时测量结果为 0
            mWidth = flCameraFocus.getMeasuredWidth() / 2;
            mHeight = flCameraFocus.getMeasuredHeight() / 2;
            Log.d("onWindowFocusChanged", "mWidth:" + mWidth);
            Log.d("onWindowFocusChanged", "mHeight:" + mHeight);
        }
    }

    private void initListeners() {
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.getInstance().takePicture();
            }
        });
        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera();
            }
        });
        btnSwitchFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.getInstance().setFlashMode((ImageView) v);
            }
        });

        verifySurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (CameraUtils.getInstance().getCameraInfo().facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    return mGestureDetector.onTouchEvent(event);
                } else {
                    return false;
                }
            }
        });
    }

    private void changeCamera() {
        CameraUtils.getInstance().stopPreview();
        int newCameraId = (CameraUtils.getInstance().getCameraId() + 1) % 2;
        CameraUtils.getInstance().openCamera(newCameraId);
        CameraUtils.getInstance().startPreview(verifySurfaceView.getHolder());
        if (newCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            btnSwitchCamera.setImageResource(R.mipmap.camera_setting_switch_back);
            btnSwitchFlash.setVisibility(View.VISIBLE);
        } else {
            btnSwitchCamera.setImageResource(R.mipmap.camera_setting_switch_front);
            btnSwitchFlash.setVisibility(View.GONE);
        }
    }

    private void startDetect() {
        Camera.Parameters params = CameraUtils.getInstance().getCameraParameters();
        Camera camera = CameraUtils.getInstance().getCamera();
        if (params.getMaxNumDetectedFaces() > 0) {
            if (mFaceView != null) {
                mFaceView.clearFaces();
                mFaceView.setVisibility(View.GONE);
            }
        }
        camera.setFaceDetectionListener(new FaceDetectionListenerImpl(mHandler));
        camera.startFaceDetection();
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_face;
    }

    static class MainHandler extends Handler {

        private WeakReference<Activity> mActivity;

        MainHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final FaceActivity activity = (FaceActivity) mActivity.get();
            switch (msg.what) {
                case CameraUtils.MSG_PREVIEW_STARTED:
                    activity.startDetect();
                    break;
                case CameraUtils.MSG_SCAN_FACE:
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Camera.Face[] faces = (Camera.Face[]) msg.obj;
                            activity.mFaceView.setFaces(faces);
                        }
                    });
                    break;
            }
        }
    }

    private GestureDetector mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            CameraUtils.getInstance().autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Log.e("FaceActivity", "聚焦成功");
                    } else {
                        Log.e("FaceActivity", "聚焦失败");
                    }
                    flCameraFocus.setVisibility(View.GONE);
                }
            });
            CameraUtils.getInstance().setFocusArea(FaceActivity.this, event);
            showFocusLayout(event);
            return true;
        }
    });

    private void showFocusLayout(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flCameraFocus.getLayoutParams();
        params.leftMargin = (int) (x - mWidth + 0.5);
        params.topMargin = (int) (y - mHeight + 0.5 + rlSettings.getHeight());
        flCameraFocus.requestLayout(); // flCameraFocus.setLayoutParams(params);
        flCameraFocus.setVisibility(View.VISIBLE);
    }
}
