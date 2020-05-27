package com.sleticalboy.dailywork.components.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private int mCount;
    private Thread mWorker;
    private boolean mStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
        mWorker = new Thread(() -> {
            while (mStarted) {
                Log.d(TAG, "mCount++:" + mCount++);
                try {
                    Thread.sleep(150L);
                } catch (InterruptedException e) {
                    Log.e(TAG, "onStartCommand: error", e);
                }
            }
        });
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        if (!mStarted) {
            mWorker.start();
            mStarted = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onRebind(final Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStarted = false;
        Log.d(TAG, "onDestroy() called");
    }
}
