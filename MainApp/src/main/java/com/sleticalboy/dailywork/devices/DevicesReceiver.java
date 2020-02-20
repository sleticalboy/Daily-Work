package com.sleticalboy.dailywork.devices;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 20-2-17.
 *
 * @author sleticalboy
 * @description
 */
public final class DevicesReceiver extends DeviceAdminReceiver {

    private static final String TAG = "DevicesReceiver";

    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        Log.d(TAG, "onEnabled() called with: context = [" + context + "], intent = [" + intent + "]");
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        Log.d(TAG, "onDisabled() called with: context = [" + context + "], intent = [" + intent + "]");
    }

    @Nullable
    @Override
    public CharSequence onDisableRequested(@NonNull Context context, @NonNull Intent intent) {
        Log.d(TAG, "onDisableRequested() called with: context = [" + context + "], intent = [" + intent + "]");
        return null;
    }
}
