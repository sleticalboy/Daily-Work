package com.sleticalboy.dailywork.bt.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created on 20-8-17.
 *
 * @author Ben binli@grandstream.cn
 */
public final class BtScanner {

    private static final String TAG = "BtScanner";

    private final Context mContext;
    private Callback mCallback;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
                        == BluetoothAdapter.STATE_ON) {
                    startScan(mCallback);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // discovery started
                Log.d(TAG, "receive action: " + action + ", discovery started");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // discovery finished
                Log.d(TAG, "receive action: " + action + ", discovery finished");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // bt device found
                if (mCallback != null) {
                    mCallback.onDeviceFound(getDevice(intent),
                            intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0)
                    );
                }
            }
        }

        private BluetoothDevice getDevice(Intent intent) {
            return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        }
    };

    public BtScanner(Context context) {
        final IntentFilter filters = new IntentFilter();
        filters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filters.addAction(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filters);
        mContext = context.getApplicationContext();
    }

    public boolean startScan(Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback is null.");
        }
        mCallback = callback;
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.getState() == BluetoothAdapter.STATE_ON) {
            return adapter.startDiscovery();
        } else {
            adapter.enable();
        }
        return false;
    }

    public boolean stopScan() {
        return BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    public void destroy() {
        stopScan();
        mCallback = null;
        mContext.unregisterReceiver(mReceiver);
    }

    public interface Callback {

        void onDeviceFound(BluetoothDevice device, int rssi);
    }
}
