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
import com.example.camera.util.CameraUtil;
import com.example.camera.util.FaceView;
import com.example.camera.util.GoogleDetectListenerImpl;

public class CameraActivity extends Activity {

    private MySurfacePreview mySurfacePreview;
    private ImageButton takeBtn;
    private FrameLayout focusLayout;
    private ImageView changeFlashModeIV;
    private ImageView switchCameraIV;
    private RelativeLayout settingRl;
    private FaceView faceView;
    int width;
    int height;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        bindListeners();
    }

    private void initView() {
        mySurfacePreview = (MySurfacePreview) findViewById(R.id.my_surfaceview);
        mySurfacePreview.setHandler(mainHandler);
        takeBtn = (ImageButton) findViewById(R.id.take_btn);
        focusLayout = (FrameLayout) findViewById(R.id.camera_focus_layout);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        Log.d("showFocusIcon initview", "w " + w + " h " + h);
        focusLayout.measure(w, h);
        width = focusLayout.getMeasuredWidth() / 2;
        height = focusLayout.getMeasuredHeight() / 2;
        Log.d("showFocusIcon initview", "focusLayout.getMeasuredWidth()/2"
                + focusLayout.getMeasuredWidth() / 2 + "focusLayout.getMeasuredHeight()/2"
                + focusLayout.getMeasuredHeight() / 2);
        changeFlashModeIV = (ImageView) findViewById(R.id.flash_iv);
        switchCameraIV = (ImageView) findViewById(R.id.swich_camera_iv);
        settingRl = (RelativeLayout) findViewById(R.id.setting_rl);
        faceView = (FaceView) findViewById(R.id.face_view);

    }

    private void bindListeners() {
        takeBtn.setOnClickListener(new TakeBtnClickListener());
        mySurfacePreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (CameraUtil.getInstance().getCameraInfo().facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    return gestureDetector.onTouchEvent(event);
                } else {
                    return false;
                }
            }
        });

        changeFlashModeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtil.getInstance().setFlashMode(changeFlashModeIV);
            }
        });

        switchCameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera();
            }
        });
    }

    private class TakeBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CameraUtil.getInstance().doTakePic();
        }
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
            CameraUtil.getInstance().autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Log.d("renlei", "聚焦成功");
                    } else {
                        Log.d("renlei", "聚焦失败");

                    }
                    focusLayout.setVisibility(View.GONE);
                }
            });
            CameraUtil.getInstance().setFocusArea(CameraActivity.this, e);
            showFocusIcon(e);
            return true;
        }
    });

    private void showFocusIcon(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) focusLayout.getLayoutParams();
        params.leftMargin = (int) (x - width + 0.5);
        params.topMargin = (int) (y - height + 0.5 + settingRl.getHeight());
//        Log.d("showFocusIcon","focusLayout.getMeasuredWidth()/2"+focusLayout.getMeasuredWidth()/2
// +"focusLayout.getMeasuredHeight()/2"+focusLayout.getMeasuredHeight()/2);
//        Log.d("showFocusIcon","focusLayout.getWidth()/2"+focusLayout.getWidth()/2
// +"focusLayout.getHeight()/2"+focusLayout.getHeight()/2);
        Log.d("showFocusIcon", "x" + x + "y" + y + "params.width" + params.width
                + "params.height" + params.height);
//        focusLayout.setLayoutParams(params);
        focusLayout.requestLayout();
//        focusLayout.setLayoutParams(params);
        focusLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) focusLayout.getLayoutParams();
        Log.d("showFocusIcon", "x" + x + "y" + y + "params2.width" + params2.width
                + "params2.height" + params2.height);
    }

    public void changeCamera() {
        CameraUtil.getInstance().doStopPreview();
        int newCameraId = (CameraUtil.getInstance().getCameraId() + 1) % 2;
        CameraUtil.getInstance().doOpenCamera(newCameraId);
        CameraUtil.getInstance().doStartPreview(mySurfacePreview.getHolder());
        if (newCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchCameraIV.setImageResource(R.drawable.camera_setting_switch_back);
            changeFlashModeIV.setVisibility(View.VISIBLE);
        } else {
            switchCameraIV.setImageResource(R.drawable.camera_setting_switch_front);
            changeFlashModeIV.setVisibility(View.GONE);
        }
    }

    private MainHandler mainHandler = new MainHandler();

    private void startGoogleDetect() {
        Camera.Parameters parameters = CameraUtil.getInstance().getCameraParameters();
        Camera camera = CameraUtil.getInstance().getCamera();
        if (parameters.getMaxNumDetectedFaces() > 0) {
            if (faceView != null) {
                faceView.clearFaces();
                faceView.setVisibility(View.VISIBLE);
            }
            camera.setFaceDetectionListener(new GoogleDetectListenerImpl(CameraActivity.this, mainHandler));
            camera.startFaceDetection();
        }
    }

    private class MainHandler extends Handler {

        @Override
        public void handleMessage(final Message msg) {
            int what = msg.what;
            switch (what) {
                case CameraUtil.PREVIEW_HAS_STARTED:
                    startGoogleDetect();
                    Log.e("renlei110", "开启人脸识别");
                    break;
                case CameraUtil.RECEIVE_FACE_MSG:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Camera.Face[] faces = (Camera.Face[]) msg.obj;
                            faceView.setFaces(faces);
                            Log.e("renlei111", "收到人脸识别的信息");
                        }
                    });

                    break;
            }
            super.handleMessage(msg);
        }
    }
}
