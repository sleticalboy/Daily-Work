package com.sleticalboy.dailywork.components.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private int mCount;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
        new Thread(() -> {
            while (mCount < 120) {
                Log.d(TAG, "mCount++:" + mCount++);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Log.e(TAG, "onStartCommand: error", e);
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand() startId = [" + startId + "]");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
