package com.example.camera.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by renlei
 * DATE: 15-11-5
 * Time: 下午7:21
 * Email: lei.ren@renren-inc.com
 */
public class ImageUtil {

    public static void saveImage(File file, byte[] data, String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, options);
        int degrees = getExifRotateDegree(filePath);
    }

    public static String getSaveImgePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/renlei/"
                    + System.currentTimeMillis() + ".jpg";
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return path;
        }
        return System.currentTimeMillis() + ".jpg";
    }

    public static int getExifRotateDegree(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degrees = getExifRotateDegrees(orientation);
            Log.d("imageutil degrees", degrees + "");
            return degrees;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getExifRotateDegrees(int exifOrientation) {
        int degrees = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                degrees = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
        }
        return degrees;
    }
}
