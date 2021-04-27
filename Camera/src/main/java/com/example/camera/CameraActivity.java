package com.example.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.camera.preview.MySurfacePreview;
import com.example.camera.util.CameraInstance;
import com.example.camera.util.FaceView;
import com.example.camera.util.GoogleDetectListenerImpl;

import java.lang.ref.WeakReference;

public class CameraActivity extends Activity {

    private MySurfacePreview mySurfacePreview;
    private ImageButton takeBtn;
    private FrameLayout focusLayout;
    private ImageView changeFlashModeIV;
    private ImageView switchCameraIV;
    private RelativeLayout settingRl;
    private FaceView faceView;
    private int width;
    private int height;
    private final MainHandler mainHandler = new MainHandler(this);

    /**
     * Called when the activity was first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        bindListeners();
    }

    private void initView() {
        mySurfacePreview = findViewById(R.id.my_surfaceview);
        mySurfacePreview.setHandler(mainHandler);
        takeBtn = findViewById(R.id.take_btn);
        focusLayout = findViewById(R.id.camera_focus_layout);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        Log.d("showFocusIcon initview", "w " + w + " h " + h);
        focusLayout.measure(w, h);
        width = focusLayout.getMeasuredWidth() / 2;
        height = focusLayout.getMeasuredHeight() / 2;
        Log.d("showFocusIcon initview", "focusLayout.getMeasuredWidth()/2"
                + focusLayout.getMeasuredWidth() / 2 + "focusLayout.getMeasuredHeight()/2"
                + focusLayout.getMeasuredHeight() / 2);
        changeFlashModeIV = findViewById(R.id.flash_iv);
        switchCameraIV = findViewById(R.id.swich_camera_iv);
        settingRl = findViewById(R.id.setting_rl);
        faceView = findViewById(R.id.face_view);
    }

    private void bindListeners() {
        takeBtn.setOnClickListener(v -> CameraInstance.get().doTakePic());
        mySurfacePreview.setOnTouchListener((v, event) -> {
            if (CameraInstance.get().getCameraInfo().facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return gestureDetector.onTouchEvent(event);
            } else {
                return false;
            }
        });

        changeFlashModeIV.setOnClickListener(v -> CameraInstance.get().setFlashMode(changeFlashModeIV));

        switchCameraIV.setOnClickListener(v -> changeCamera());
    }

    GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("MyGestureDetector", "onDown");
            return true;
        }

        @Override
        public boolean onSingleTapUp(final MotionEvent e) {
            Log.d("MyGestureDetector", "onSingleTapUp");
            CameraInstance.get().autoFocus((success, camera) -> {
                if (success) {
                    Log.d("renlei", "聚焦成功");
                } else {
                    Log.d("renlei", "聚焦失败");

                }
                focusLayout.setVisibility(View.GONE);
            });
            CameraInstance.get().setFocusArea(CameraActivity.this, e);
            showFocusIcon(e);
            return true;
        }
    });

    private void showFocusIcon(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) focusLayout.getLayoutParams();
        lp.leftMargin = (int) (x - width + 0.5);
        lp.topMargin = (int) (y - height + 0.5 + settingRl.getHeight());
        Log.d("showFocusIcon", "x" + x + "y" + y + "params.width" + lp.width
                + "params.height" + lp.height);
        focusLayout.setLayoutParams(lp);
        focusLayout.setVisibility(View.VISIBLE);
    }

    public void changeCamera() {
        CameraInstance.get().doStopPreview();
        int newCameraId = (CameraInstance.get().getCameraId() + 1) % 2;
        CameraInstance.get().doOpenCamera(newCameraId);
        CameraInstance.get().doStartPreview(mySurfacePreview.getHolder());
        if (newCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchCameraIV.setImageResource(R.drawable.camera_setting_switch_back);
            changeFlashModeIV.setVisibility(View.VISIBLE);
        } else {
            switchCameraIV.setImageResource(R.drawable.camera_setting_switch_front);
            changeFlashModeIV.setVisibility(View.GONE);
        }
    }

    private void startGoogleDetect() {
        Camera.Parameters parameters = CameraInstance.get().getCameraParameters();
        Camera camera = CameraInstance.get().getCamera();
        if (parameters.getMaxNumDetectedFaces() > 0) {
            if (faceView != null) {
                faceView.clearFaces();
                faceView.setVisibility(View.VISIBLE);
            }
            camera.setFaceDetectionListener(new GoogleDetectListenerImpl(mainHandler));
            camera.startFaceDetection();
        }
    }

    private static class MainHandler extends Handler {

        private final WeakReference<CameraActivity> mHost;

        MainHandler(CameraActivity host) {
            mHost = new WeakReference<>(host);
        }

        @Override
        public void handleMessage(final Message msg) {
            CameraActivity host = mHost.get();
            if (host == null) return;
            switch (msg.what) {
                case CameraInstance.PREVIEW_HAS_STARTED:
                    host.startGoogleDetect();
                    Log.e("renlei110", "开启人脸识别");
                    break;
                case CameraInstance.RECEIVE_FACE_MSG:
                    host.faceView.setFaces((Camera.Face[]) msg.obj);
                    Log.e("renlei111", "收到人脸识别的信息");
                    break;
            }
        }
    }
}
