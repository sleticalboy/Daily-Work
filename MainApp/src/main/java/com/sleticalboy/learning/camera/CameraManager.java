package com.sleticalboy.learning.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-2-27.
 * <p>
 * CameraManager
 *
 * @author leebin
 * @version 1.0
 */
public final class CameraManager {

    private static final String TAG = "CameraManager";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    private static final CameraManager MANAGER = new CameraManager();
    private Camera mCamera;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static CameraManager getInstance() {
        synchronized (MANAGER) {
            return MANAGER;
        }
    }

    /**
     * 开启预览
     *
     * @param surface
     */
    public void startPreview(SurfaceTexture surface) {
        try {
            if (mCamera == null) {
                openCamera();
            }
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开相机
     */
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
     * @param dir
     * @param callback
     */
    public void takePicture(final File dir, final OnPictureTakenCallback callback) {
        mCamera.takePicture(() -> {
            // do nothing
        }, null, (data, camera) -> {
            CameraManager.this.onPictureTaken(dir, data, callback);
            camera.startPreview();
        });
    }

    private void onPictureTaken(File dir, byte[] data, OnPictureTakenCallback callback) {
        if (callback == null || dir == null) {
            throw new RuntimeException("OnPictureTakenCallback and dir can not be null");
        }
        new Thread(() -> {
            final File file = new File(dir, System.currentTimeMillis() + ".jpg");
            BufferedOutputStream bos;
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
                }
            } catch (Exception e) {
                callback.onFailure(e);
            }
        }).start();
    }

    public void autoFocus(final Context context, final ViewGroup focusView,
                          final Size focusViewSize, final MotionEvent event) {
        mCamera.autoFocus((success, camera) -> {
            if (success) {
                // 1, 设置聚焦区域
                setFocusArea(context, event);
                // 2, 显示聚焦图标
                showFocusIcon(focusView, event, focusViewSize);
                Log.e(TAG, "onAutoFocus: 聚焦成功");
            } else {
                Log.e(TAG, "onAutoFocus: 聚焦失败");
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
        mHandler.postDelayed(() -> focusView.setVisibility(View.GONE), 100);
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
            // CameraManager.getInstance().startPreview(surface);
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
