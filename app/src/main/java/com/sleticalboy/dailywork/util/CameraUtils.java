package com.sleticalboy.dailywork.util;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import com.sleticalboy.dailywork.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-2-24.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class CameraUtils {

    private static final CameraUtils INSTANCE = new CameraUtils();
    public static final int MSG_PREVIEW_STARTED = 1000;
    public static final int MSG_SCAN_FACE = 1001;

    private boolean mIsPreview = false;
    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public static CameraUtils getInstance() {
        synchronized (INSTANCE) {
            return INSTANCE;
        }
    }

    /**
     * open camera
     *
     * @param cameraId see {@link android.hardware.Camera.CameraInfo#CAMERA_FACING_BACK} and
     *                 {@link android.hardware.Camera.CameraInfo#CAMERA_FACING_FRONT}
     */
    public void openCamera(int cameraId) {
        mCameraId = cameraId;
        try {
            mCamera = Camera.open(cameraId);
            Camera.getCameraInfo(cameraId, mCameraInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stop preview
     */
    public void stopPreview() {
        if (mIsPreview) {
            mIsPreview = false;
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
    }

    /**
     * start preview
     *
     * @param holder {@link SurfaceHolder} object
     */
    public void startPreview(SurfaceHolder holder) {
        if (mIsPreview) {
            mCamera.stopPreview();
        }
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            params.setPictureFormat(ImageFormat.JPEG);
            mCamera.setDisplayOrientation(90);
            if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            Camera.Size supportedPreviewSizes = params.getSupportedPreviewSizes().get(0);
            params.setPreviewSize(supportedPreviewSizes.width, supportedPreviewSizes.height);

            Camera.Size supportedPictureSizes = params.getSupportedPictureSizes().get(0);
            params.setPictureSize(supportedPictureSizes.width, supportedPictureSizes.height);

            mCamera.setParameters(params);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mIsPreview = true;
        }
    }

    /**
     * take photos
     */
    public void takePicture() {
        if (mIsPreview && mCamera != null) {
            mCamera.takePicture(new Camera.ShutterCallback() {
                @Override
                public void onShutter() {

                }
            }, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    mIsPreview = false;
                    saveImage(data);
                }
            });
            mCamera.startPreview();
            mIsPreview = true;
        }
    }

    /**
     * save image
     *
     * @param data byte array data of the image
     */
    private void saveImage(byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = ImageUtils.INSTANCE.getSaveImagePath();
                final File file = new File(path);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file, true);
                    fos.write(data);
                    ImageUtils.INSTANCE.saveImage(file, data, path);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * auto focus
     *
     * @param autoFocusCallback {@link Camera.AutoFocusCallback} object
     * @return
     */
    public boolean autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        final Camera.Parameters params = getCameraParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            if (!Camera.Parameters.FOCUS_MODE_AUTO.equals(params.getFocusMode())) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(params);
            }
            if (autoFocusCallback != null) {
                mCamera.autoFocus(autoFocusCallback);
            }
            return true;
        }
        return false;
    }

    /**
     * set click to focus area
     *
     * @param context {@link Context} object
     * @param event   {@link MotionEvent} object
     */
    public void setFocusArea(Context context, MotionEvent event) {
        if (!isSupportAutoFocusArea() || mCamera == null) {
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        int ax = (int) (2000f * event.getRawX() / context.getResources().getDisplayMetrics().widthPixels - 1000);
        int ay = (int) (2000f * event.getRawY() / context.getResources().getDisplayMetrics().heightPixels - 1000);
        // 防止超出1000 ，-1000的范围
        if (ay > 900) {
            ay = 900;
        } else if (ay < -900) {
            ay = -900;
        }
        if (ax > 900) {
            ax = 900;
        } else if (ax < -900) {
            ax = -900;
        }
        Rect rect = new Rect(ax - 100, ay - 100, ax + 100, ay + 100);
        Camera.Area area = new Camera.Area(rect, 1000);
        List<Camera.Area> focusAreas = new ArrayList<>();
        focusAreas.add(area);
        params.setFocusAreas(focusAreas);
        params.setMeteringAreas(focusAreas);
        mCamera.setParameters(params);
    }

    public static boolean isSupportAutoFocusArea() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * set flash mode
     *
     * @param target the {@link ImageView} clicked to switch flash mode
     */
    public void setFlashMode(ImageView target) {
        final Camera.Parameters params = getCameraParameters();
        String flashMode = params.getFlashMode();
        if (flashMode != null) {
            switch (flashMode) {
                case Camera.Parameters.FLASH_MODE_OFF:
                    target.setImageResource(R.mipmap.camera_setting_flash_on_normal);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    break;
                case Camera.Parameters.FLASH_MODE_ON:
                    target.setImageResource(R.mipmap.camera_setting_flash_auto_normal);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    break;
                case Camera.Parameters.FLASH_MODE_AUTO:
                    target.setImageResource(R.mipmap.camera_setting_flash_off_normal);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    break;
                default:
                    target.setImageResource(R.mipmap.camera_setting_flash_off_normal);
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    break;
            }
            mCamera.setParameters(params);
        }
    }

    public int getCameraId() {
        return mCameraId;
    }

    public Camera.CameraInfo getCameraInfo() {
        return mCameraInfo;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public Camera.Parameters getCameraParameters() {
        if (mCamera != null) {
            return mCamera.getParameters();
        }
        return null;
    }
}
