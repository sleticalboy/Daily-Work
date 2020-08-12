package com.sleticalboy.dailywork.bt.core;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BleService extends Service implements Handler.Callback {

    private static final int MSG_START_SCAN = 0x20;
    private static final int MSG_STOP_SCAN = 0x22;
    private static final int MSG_CONNECT_GATT = 0x23;

    private LeBinder mBinder;
    private Handler mCoreHandler;
    private BleScanner mScanner;

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("BleCoreThread");
        thread.start();
        mCoreHandler = new Handler(thread.getLooper(), this);
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

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_START_SCAN) {
            mScanner.startScan(((BleScanner.Request) msg.obj));
        } else if (msg.what == MSG_STOP_SCAN) {
            mScanner.stopScan();
        } else if (msg.what == MSG_CONNECT_GATT) {
            connectGatt(((BluetoothDevice) msg.obj));
        }
        return true;
    }

    private void connectGatt(BluetoothDevice device) {
        device.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                //
            }
        });
    }

    public static class LeBinder extends Binder {

        private final BleService mService;

        public LeBinder(BleService service) {
            mService = service;
        }

        public Handler getHandler() {
            return mService.mCoreHandler;
        }

        public void startScan(BleScanner.Request request) {
            mService.mScanner.startScan(request);
            final Message msg = Message.obtain();
            msg.obj = request;
            msg.what = MSG_START_SCAN;
            getHandler().sendMessage(msg);
        }

        public void stopScan() {
            getHandler().sendEmptyMessage(MSG_STOP_SCAN);
        }

        public void connectGatt(BluetoothDevice device) {
            final Message msg = Message.obtain();
            msg.obj = device;
            msg.what = MSG_CONNECT_GATT;
            getHandler().sendMessage(msg);
        }
    }
}
