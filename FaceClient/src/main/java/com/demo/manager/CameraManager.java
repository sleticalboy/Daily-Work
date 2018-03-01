package com.demo.manager;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;

import com.demo.BuildConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public static CameraManager getInstance() {
        synchronized (MANAGER) {
            return MANAGER;
        }
    }

    // 打开相机
    private void openCamera() {
        int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mCamera = Camera.open(cameraId);
//        Camera.getCameraInfo(cameraId, new Camera.CameraInfo());
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
