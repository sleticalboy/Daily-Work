package com.sleticalboy.dailywork.util;

import android.content.Context;

/**
 * Created on 18-6-11.
 *
 * @author leebin
 * @description 单位转换工具
 */
public final class DimenUtils {

    public static int dpToPx(Context context, int dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }

    public static int screenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int screenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
