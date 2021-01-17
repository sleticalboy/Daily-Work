package com.sleticalboy.sample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created on 21-1-5.
 *
 * @author binli
 */
public class SampleService extends Service {

    private static final String TAG = "SampleService";

    private final IBinder mBinder = new com.sleticalboy.sample.service.ITest.Stub() {
        @Override
        public void doWrite(DataStruct data) throws RemoteException {
            Log.d(TAG, "doWrite() data = [" + data + "]");
        }

        @Override
        public void doRead(String name, boolean notify) throws RemoteException {
            Log.d(TAG, "doRead() name = [" + name + "], notify = [" + notify + "]");
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called ");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind() called ");
    }
}
