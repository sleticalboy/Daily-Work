package com.sleticalboy.dailywork.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created on 18-2-3.
 *
 * @author sleticalboy
 * @version 1.0
 * @description utils for operations about UI
 */
public class UIUtils {

    /**
     * 获取屏幕的尺寸
     *
     * @param context
     * @return
     */
    public static Size getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        float density = displayMetrics.density;
        int width = (int) (displayMetrics.widthPixels * density);
        int height = (int) (displayMetrics.heightPixels * density);
        return new Size(width, height);
    }

    /**
     * dp to px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * px to dp
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * sp to px
     *
     * @param context
     * @param sp
     * @return
     */
    public static int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public static class Size {

        private final int mWidth;
        private final int mHeight;

        Size(int width, int height) {
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
}
