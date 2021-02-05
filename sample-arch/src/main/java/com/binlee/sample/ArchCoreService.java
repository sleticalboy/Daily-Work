package com.binlee.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class ArchCoreService extends Service {

    private final IFunctions mFunc;

    public ArchCoreService() {
        mFunc = new ArchManager();
    }

    @Override
    public void onCreate() {
        mFunc.init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFunc.onStart();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mFunc.onDestroy();
    }
}
