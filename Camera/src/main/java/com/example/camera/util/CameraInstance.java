package com.example.camera.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import com.example.camera.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午4:57
 * Email: lei.ren@renren-inc.com
 */
public class CameraInstance {

    private Camera mCamera;
    private static CameraInstance sCamera = new CameraInstance();
    private boolean isPreview;
    private int cameraId = -1; //0表示后置，1表示前置
    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    public static final int PREVIEW_HAS_STARTED = 110;
    public static final int RECEIVE_FACE_MSG = 111;

    public static CameraInstance get() {
        return sCamera;
    }

    /**
     * 打开相机
     *
     * @param cameraId
     */
    public void doOpenCamera(int cameraId) {
        Log.d("renlei", "open camera" + cameraId);
        try {
            this.cameraId = cameraId;
            mCamera = Camera.open(cameraId);
            Camera.getCameraInfo(cameraId, mCameraInfo);///这里的mCamerainfo必须是new出来的，不能是个null
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启预览
     *
     * @param holder
     */
    public void doStartPreview(SurfaceHolder holder) {
        Log.d("CAmerautil", "doStartPreview");
        if (isPreview) {
            mCamera.stopPreview();
        }
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);//设置照片拍摄后的保存格式
            mCamera.setDisplayOrientation(90);//否则方向会有问题
            //前置与后置的不一样，这里暂时只设置前置的，后置的可以相应的去设置
            if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            printSupportPreviewSize(parameters);
            printSupportPictureSize(parameters);
            printSupportFocusMode(parameters);
//            parameters.setPictureSize(parameters.getPreviewSize().width,parameters.getPictureSize().height);
            //设置的这两个size必须时支持的size大小，否则时不可以的，会出现setparameters错误
            parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(0).width,
                    parameters.getSupportedPreviewSizes().get(0).height);
            parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width,
                    parameters.getSupportedPictureSizes().get(0).height);
            mCamera.setParameters(parameters);
            Camera.Parameters mParams = mCamera.getParameters();
            Log.i("renlei", "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i("renlei", "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

    /**
     * 结束预览
     */
    public void doStopPreview() {
        if (isPreview) {
            isPreview = false;
            mCamera.stopPreview();

            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void doTakePic() {
        if (isPreview && mCamera != null) {
            mCamera.takePicture(new ShutCallBackImpl(), null, new PicCallBacKImpl());
        }
    }

    /**
     * 拍照时的动作
     * 默认会有咔嚓一声
     */
    private static class ShutCallBackImpl implements Camera.ShutterCallback {
        @Override
        public void onShutter() {
        }
    }

    /**
     * 拍照后的最主要的返回
     */
    private class PicCallBacKImpl implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            isPreview = false;
            new Thread(() -> {
                String filePath = ImageUtil.getSaveImgePath();
                File file = new File(filePath);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file, true);
                    fos.write(data);
                    ImageUtil.saveImage(file, data, filePath);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            //重新开启预览 ，不然不能继续拍照
            mCamera.startPreview();
            isPreview = true;
        }
    }

    /**
     * 打印支持的previewSizes
     *
     * @param params
     */
    public void printSupportPreviewSize(Camera.Parameters params) {
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size size = previewSizes.get(i);
            Log.i("camerautil", "previewSizes:width = " + size.width + " height = " + size.height);
        }
    }

    /**
     * 打印支持的pictureSizes
     *
     * @param params
     */
    public void printSupportPictureSize(Camera.Parameters params) {
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Camera.Size size = pictureSizes.get(i);
            Log.i("camerautil", "pictureSizes:width = " + size.width
                    + " height = " + size.height);
        }
    }

    /**
     * 点击聚焦
     *
     * @param autoFocusCallback
     * @return
     */
    public boolean autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        Log.d("Camerrautil", "autoFouce");
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportMode = parameters.getSupportedFocusModes();
        if (supportMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            String focusMode = parameters.getFocusMode();
            if (!Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(parameters);
            }
            if (autoFocusCallback != null) {
                mCamera.autoFocus(autoFocusCallback);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置聚焦的区域
     *
     * @param mContext
     * @param event
     */
    public void setFocusArea(Context mContext, MotionEvent event) {
        if (!CameraInstance.isSupportFocusArea() || mCamera == null) return;
        Camera.Parameters parameters = mCamera.getParameters();
        int ax = (int) (2000f * event.getRawX() / mContext.getResources().getDisplayMetrics().widthPixels - 1000);
        int ay = (int) (2000f * event.getRawY() / mContext.getResources().getDisplayMetrics().heightPixels - 1000);
//        Log.d("renlei",parameters.getMaxNumFocusAreas()+"");
        int rawx = (int) event.getRawX();
        int rawy = (int) event.getRawY();
        Log.d("renlei", "widthpix" + mContext.getResources().getDisplayMetrics().widthPixels + "heightpix"
                + mContext.getResources().getDisplayMetrics().heightPixels);
        Log.d("renlei", "rawx" + rawx + "rawy" + rawy);
        //防止超出1000 ，-1000的范围
        if (ay > 900) {
            ay = 900;
        } else if (ay < -900) {
            ay = -900;
        }

        if (ax < -900) {
            ax = -900;
        } else if (ax > 900) {
            ax = 900;
        }
        Log.d("renlei09", "ax" + ax + "ay" + ay);
        Camera.Area area = new Camera.Area(
                new Rect(ax - 100, ay - 100, ax + 100, ay + 100), 1000);
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        areas.add(area);
        parameters.setFocusAreas(areas);
        parameters.setMeteringAreas(areas);
        mCamera.setParameters(parameters);
    }

    /**
     * 是否符合设置对焦区域的SDK版本
     *
     * @return
     */
    public static boolean isSupportFocusArea() {
        return Build.VERSION.SDK_INT >= 14;
    }

    /**
     * 设置闪光灯的模式
     *
     * @param imageView
     */
    public void setFlashMode(ImageView imageView) {
        Camera.Parameters parameters = mCamera.getParameters();
        String flashMode = parameters.getFlashMode();
        Log.d("setFlashMode  ", flashMode);
        if (flashMode != null) {
            if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
                imageView.setImageResource(R.drawable.camera_setting_flash_on_normal);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
                imageView.setImageResource(R.drawable.camera_setting_flash_auto_normal);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            } else if (flashMode.equals(Camera.Parameters.FLASH_MODE_AUTO)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                imageView.setImageResource(R.drawable.camera_setting_flash_off_normal);
            } else {
                imageView.setImageResource(R.drawable.camera_setting_flash_off_normal);
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(parameters);
        }
    }

    public int getCameraId() {
        return cameraId;
    }

    /**
     * 打印支持的聚焦模式
     *
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
            Log.i("CameraUtil", "focusModes--" + mode);
        }
    }

    public Camera.CameraInfo getCameraInfo() {
        return mCameraInfo;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public Camera.Parameters getCameraParameters() {
        if (mCamera != null) return mCamera.getParameters();
        return null;
    }
}
