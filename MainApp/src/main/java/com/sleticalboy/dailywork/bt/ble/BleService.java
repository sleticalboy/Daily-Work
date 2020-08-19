package com.sleticalboy.dailywork.bt.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

public final class BleService extends Service implements Handler.Callback {

    private static final String TAG = "BleService";

    public static final String ACTION_HID_CONNECTION_STATE_CHANGED =
            "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED";

    private static final int MSG_START_SCAN = 0x20;
    private static final int MSG_STOP_SCAN = 0x22;
    private static final int MSG_START_CONNECT = 0x23;
    private static final int MSG_CANCEL_CONNECT = 0x24;

    private LeBinder mBinder;
    private Handler mCoreHandler;
    private BleScanner mScanner;
    private Dispatcher mDispatcher;
    private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // all logic is handled in mCoreHandler thread
            final String action = intent.getAction();
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.BOND_NONE) == BluetoothDevice.BOND_BONDED) {
                    Log.e(TAG, "receive action: " + action + " start notify connection state.");
                    mDispatcher.notifyConnectionState(device);
                }
            } else if (ACTION_HID_CONNECTION_STATE_CHANGED.equals(action)) {
                Log.e(TAG, "receive action: " + action + " will notify connection state.");
                // mDispatcher.notifyConnectionState(device);
            }
        }
    };

    @Override
    public void onCreate() {
        final HandlerThread thread = new HandlerThread("BleCoreThread");
        thread.start();
        mCoreHandler = new Handler(thread.getLooper(), this);
        mScanner = new BleScanner(this, mCoreHandler);
        mDispatcher = new Dispatcher(this);
        bindHidProxy();
        registerBtReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @NonNull
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
        unbindHidProxy();
        unregisterBtReceiver();
    }

    private void bindHidProxy() {
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

    private void unbindHidProxy() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.closeProfileProxy(4/*BluetoothProfile.HID_HOST*/, mDispatcher.getHidHost());
    }

    private void registerBtReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(ACTION_HID_CONNECTION_STATE_CHANGED);
        registerReceiver(mBtReceiver, filter, null, mCoreHandler);
    }

    private void unregisterBtReceiver() {
        unregisterReceiver(mBtReceiver);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_START_SCAN) {
            mScanner.startScan(((BleScanner.Request) msg.obj));
        } else if (msg.what == MSG_STOP_SCAN) {
            mScanner.stopScan();
        } else if (msg.what == MSG_START_CONNECT) {
            mDispatcher.enqueue((Connection) msg.obj);
        } else if (msg.what == MSG_CANCEL_CONNECT) {
            if (msg.obj instanceof BluetoothDevice) {
                mDispatcher.cancel(((BluetoothDevice) msg.obj));
            } else {
                mDispatcher.cancelAll();
            }
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

        public void connect(BluetoothDevice device, IConnectCallback callback) {
            final Message msg = Message.obtain();
            msg.obj = new Connection(device, callback);
            msg.what = MSG_START_CONNECT;
            getHandler().sendMessage(msg);
        }

        public void cancel(BluetoothDevice device) {
            final Message msg = Message.obtain();
            msg.what = MSG_CANCEL_CONNECT;
            msg.obj = device;
            getHandler().sendMessage(msg);
        }
    }
}
