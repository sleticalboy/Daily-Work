package com.sleticalboy.dailywork.bt.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BleService extends Service {

    private LeBinder mBinder;
    private Handler mCoreHandler;
    private BleScanner mScanner;

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("BleCoreThread");
        thread.start();
        mCoreHandler = new Handler(thread.getLooper());
        mScanner = new BleScanner(this, mCoreHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LeBinder(this);
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static class LeBinder extends Binder {

        private final BleService mService;

        public LeBinder(BleService service) {
            mService = service;
        }

        public void startScan(BleScanner.Callback callback) {
            mService.mScanner.startScan(callback);
        }

        public Handler getHandler() {
            return mService.mCoreHandler;
        }
    }
}
