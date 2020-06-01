package com.sleticalboy.dailywork.components.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class LocalService extends Service {

    private static final String TAG = "LocalService";

    private LocalBinder mBinder;
    private OnUnbindCallback mUnbindCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with startId: " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called intent: " + intent);
        if (mBinder == null) {
            mBinder = new LocalBinder(this);
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called with intent: " + intent);
        if (mUnbindCallback != null) {
            mUnbindCallback.onServiceUnbind();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    public void foo(OnUnbindCallback callback) {
        Log.d(TAG, "foo() called");
        mUnbindCallback = callback;
    }

    public static class LocalBinder extends Binder {

        private final LocalService mService;

        public LocalBinder(LocalService service) {
            mService = service;
        }

        public LocalService getService() {
            return mService;
        }
    }

    public interface OnUnbindCallback {

        void onServiceUnbind();
    }
}
