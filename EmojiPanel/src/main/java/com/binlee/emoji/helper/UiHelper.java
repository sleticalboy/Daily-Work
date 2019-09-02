package com.binlee.emoji.helper;

import android.util.DisplayMetrics;

public class UiHelper {

    private UiHelper() {
        throw new AssertionError("on instance.");
    }

    public static DisplayMetrics displayMetrics() {
        return OsHelper.app().getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度 单位：像素
     *
     * @return 屏幕宽度
     */
    public static int screenWidth() {
        return displayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度 单位：像素
     *
     * @return 屏幕高度
     */
    public static int screenHeight() {
        return displayMetrics().heightPixels;
    }

    public static int dip2px(float dipValue){
        final float scale = displayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
}
