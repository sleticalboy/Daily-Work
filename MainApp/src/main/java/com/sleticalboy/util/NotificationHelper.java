package com.sleticalboy.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

/**
 * Created on 20-4-2.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class NotificationHelper {

    public static final String COMMON_TAG = "Daily-Work";
    public static final String SPECIAL_TAG = "Special-Notify";

    private final Context mContext;
    private static NotificationManager sManager;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public static void createAllChannels(Context context) {
        ensureManager(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        sManager.createNotificationChannels(Arrays.asList(
                commonChannel(), messageChannel()
        ));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel commonChannel() {
        return new NotificationChannel(COMMON_TAG, COMMON_TAG, NotificationManager.IMPORTANCE_DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel messageChannel() {
        return new NotificationChannel(SPECIAL_TAG, SPECIAL_TAG, NotificationManager.IMPORTANCE_DEFAULT);
    }

    private static void ensureManager(final Context context) {
        if (sManager != null) {
            return;
        }
        sManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
