package com.sleticalboy.dailywork.manager;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sleticalboy.dailywork.BuildConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-2-27.
 *
 * @author sleticalboy
 * @version 1.0
 * @description CameraManager
 */
public final class CameraManager {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "CameraManager";
    private static final CameraManager MANAGER = new CameraManager();
    private Camera mCamera;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static CameraManager getInstance() {
        synchronized (MANAGER) {
            return MANAGER;
        }
    }

    // 打开相机
    private void openCamera() {
        int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCamera = Camera.open(cameraId);
        mCamera.setDisplayOrientation(90);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        Camera.Size pictureSize = parameters.getSupportedPictureSizes().get(0);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        Camera.Size previewSize = parameters.getSupportedPreviewSizes().get(0);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setRotation(270);

        mCamera.setParameters(parameters);
    }

    /**
     * 开启预览
     *
     * @param surface
     */
    public void startPreview(SurfaceTexture surface) {
        if (mCamera == null) {
            openCamera();
        }
        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭预览
     */
    public void stopPreview() {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /**
     * 拍照
     *
     * @param callback
     */
    public void takePicture(final OnPictureTakenCallback callback) {
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
                // do nothing
            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                CameraManager.this.onPictureTaken(data, callback);
                camera.startPreview();
            }
        });
    }

    /**
     * 设置聚焦区域
     */
    private void setFocusArea(Context context, MotionEvent event) {
        if (mCamera == null) {
            return;
        }
        int ax = (int) (2000f * event.getRawX() / context.getResources().getDisplayMetrics().widthPixels - 1000);
        int ay = (int) (2000f * event.getRawY() / context.getResources().getDisplayMetrics().heightPixels - 1000);
        if (ax > 900) {
            ax = 900;
        } else if (ax < -900) {
            ax = -900;
        }
        if (ay > 900) {
            ay = 900;
        } else if (ay < -900) {
            ay = -900;
        }
        Rect rect = new Rect(ax - 100, ay - 100, ax + 100, ay + 100);
        Camera.Area area = new Camera.Area(rect, 1000);
        List<Camera.Area> focusAreas = new ArrayList<>();
        focusAreas.add(area);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusAreas(focusAreas);
        parameters.setMeteringAreas(focusAreas);
        mCamera.setParameters(parameters);
    }

    /**
     * 显示聚焦图标
     */
    private void showFocusIcon(ViewGroup focusView, MotionEvent event, Size size) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        Log.d(TAG, "x:" + x);
        Log.d(TAG, "y:" + y);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) focusView.getLayoutParams();
        params.leftMargin = (int) (x - size.getWidth() + 0.5);
        params.topMargin = (int) (y - size.getHeight() + 0.5);
        focusView.setLayoutParams(params);
        focusView.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                focusView.setVisibility(View.GONE);
            }
        }, 100);
    }

    public void autoFocus(final Context context, final ViewGroup focusView,
                          final Size focusViewSize, final MotionEvent event) {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    // 1, 设置聚焦区域
                    setFocusArea(context, event);
                    // 2, 显示聚焦图标
                    showFocusIcon(focusView, event, focusViewSize);
                    Log.e(TAG, "onAutoFocus: 聚焦成功");
                } else {
                    Log.e(TAG, "onAutoFocus: 聚焦失败");
                }
            }
        });
    }

    private void onPictureTaken(final byte[] data, final OnPictureTakenCallback callback) {
        if (callback == null) {
            throw new RuntimeException("OnPictureTakenCallback can not be null");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = getPath();
                if (path == null) {
                    return;
                }
                File file = new File(path);
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file, true));
                    bos.write(data);
                    bos.flush();
                    bos.close();
                    if (file.exists() && file.length() > 0) {
                        if (DEBUG) {
                            Log.d(TAG, "take picture success, " + file.getPath());
                        }
                        callback.onSuccess(file);
                        file.delete();
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        }).start();
    }

    private String getPath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getPath() + File.separator
                    + System.currentTimeMillis() + ".jpg";
        }
        return null;
    }

    /**
     * 拍照完成的回调
     */
    public interface OnPictureTakenCallback {

        /**
         * 成功时时调用
         *
         * @param picture
         */
        void onSuccess(File picture);

        /**
         * 失败时调用
         *
         * @param e
         */
        void onFailure(Throwable e);
    }

    public static final class Size {

        final int mWidth;
        final int mHeight;

        public Size(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }
    }

    public static class SimpleSurfaceTextureListener implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            CameraManager.getInstance().startPreview(surface);
            if (DEBUG) {
                Log.d(TAG, "preview start");
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            CameraManager.getInstance().stopPreview();
            if (DEBUG) {
                Log.d(TAG, "preview stop");
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }
}
