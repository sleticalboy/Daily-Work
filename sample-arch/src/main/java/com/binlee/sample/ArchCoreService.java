package com.binlee.sample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ArchCoreService extends Service {

    private final IFunctions mFunc;
    private LocalBinder mBinder;

    public ArchCoreService() {
        mFunc = new ArchManager();
    }

    @Override
    public void onCreate() {
        mFunc.init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFunc.onStart();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LocalBinder();
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mFunc.onDestroy();
    }

    public final class LocalBinder extends Binder {
    }
}
