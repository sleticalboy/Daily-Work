package com.sleticalboy.dailywork.bt.core;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sleticalboy.dailywork.bt.connection.Connection;
import com.sleticalboy.dailywork.bt.connection.Dispatcher;
import com.sleticalboy.dailywork.bt.connection.IConnectCallback;

public class BleService extends Service implements Handler.Callback {

    private static final int MSG_START_SCAN = 0x20;
    private static final int MSG_STOP_SCAN = 0x22;
    private static final int MSG_CONNECT_GATT = 0x23;

    private LeBinder mBinder;
    private Handler mCoreHandler;
    private BleScanner mScanner;
    private Dispatcher mDispatcher;

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("BleCoreThread");
        thread.start();
        mCoreHandler = new Handler(thread.getLooper(), this);
        mScanner = new BleScanner(this, mCoreHandler);
        mDispatcher = new Dispatcher(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void foo() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                mDispatcher.setHidHost(proxy);
            }

            @Override
            public void onServiceDisconnected(int profile) {
                mDispatcher.setHidHost(null);
            }
        }, 4/*BluetoothProfile.HID_HOST*/);
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
            mDispatcher.enqueue((Connection) msg.obj);
        }
        return true;
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
            final Message msg = Message.obtain();
            msg.obj = request;
            msg.what = MSG_START_SCAN;
            getHandler().sendMessage(msg);
        }

        public void stopScan() {
            getHandler().sendEmptyMessage(MSG_STOP_SCAN);
        }

        public void connectGatt(BluetoothDevice device, IConnectCallback callback) {
            final Message msg = Message.obtain();
            msg.obj = new Connection(device, callback);
            msg.what = MSG_CONNECT_GATT;
            getHandler().sendMessage(msg);
        }
    }
}
