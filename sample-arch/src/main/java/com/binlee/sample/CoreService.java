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
public class CoreService extends Service {

    private IFunctions mFunc;

    @Override
    public void onCreate() {
        mFunc = new IFunctions() {
            @Override
            public Logger logger() {
                return Logger.DEFAULT;
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
