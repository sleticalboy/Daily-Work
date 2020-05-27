package com.sleticalboy.dailywork.components.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocalService extends Service {

    private LocalBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LocalBinder(this);
        }
        return mBinder;
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
}
