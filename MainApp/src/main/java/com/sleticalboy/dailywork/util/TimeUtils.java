package com.sleticalboy.dailywork.util;

import android.text.TextUtils;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created on 18-5-11.
 *
 * @author sleticalboy
 * @description 日期工具类
 */
public final class TimeUtils {

    private static final SimpleDateFormat DEFAULT_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+08:00", Locale.SIMPLIFIED_CHINESE);

    public static float getDistance(double lat1, double lng1, double lat2, double lng2) {
        lng1 *= 0.01745329251994329D;
        lat1 *= 0.01745329251994329D;
        lng2 *= 0.01745329251994329D;
        lat2 *= 0.01745329251994329D;
        double var12 = Math.sin(lng1);
        double var14 = Math.sin(lat1);
        double var16 = Math.cos(lng1);
        double var18 = Math.cos(lat1);
        double var20 = Math.sin(lng2);
        double var22 = Math.sin(lat2);
        double var24 = Math.cos(lng2);
        double var26 = Math.cos(lat2);
        double[] var28 = new double[3];
        double[] var29 = new double[3];
        var28[0] = var18 * var16;
        var28[1] = var18 * var12;
        var28[2] = var14;
        var29[0] = var26 * var24;
        var29[1] = var26 * var20;
        var29[2] = var22;
        double var30 = Math.sqrt(
                (var28[0] - var29[0]) * (var28[0] - var29[0])
                        + (var28[1] - var29[1]) * (var28[1] - var29[1])
                        + (var28[2] - var29[2]) * (var28[2] - var29[2]));
        final double var31 = (Math.asin(var30 / 2.0D) * 1.27420015798544E7D);
        return Float.parseFloat(new DecimalFormat("##.###").format(var31));
    }

    public static long str2millis(String timeStr) {
        if (!TextUtils.isEmpty(timeStr)) {
            try {
                return DEFAULT_FORMAT.parse(timeStr).getTime();
            } catch (ParseException e) {
                Log.d("TimeUtils", e.getMessage());
                return 0L;
            }
        }
        return 0L;
    }

    public static String millis2str(long time) {
        if (time > 0L) {
            return DEFAULT_FORMAT.format(new Date(time));
        }
        return null;
    }
}
