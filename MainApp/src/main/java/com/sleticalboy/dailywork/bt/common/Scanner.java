package com.sleticalboy.dailywork.bt.common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created on 20-8-17.
 *
 * @author Ben binli@grandstream.cn
 */
final class Scanner {

    private Callback mCallback;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_ON) {
                    startScan(mCallback);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // discovery started
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // discovery finished
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // bt device found
                if (mCallback != null) {
                    final int rssi = intent.getIntExtra(BluetoothDevice.EXTRA_RSSI, 0);
                    mCallback.onDeviceFound(getDevice(intent), rssi);
                }
            }
        }

        private BluetoothDevice getDevice(Intent intent) {
            return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        }
    };

    public Scanner(Context context) {
        final IntentFilter filters = new IntentFilter();
        filters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filters.addAction(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filters);
    }

    boolean startScan(Callback callback) {
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

    boolean stopScan() {
        return BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    public interface Callback {

        void onDeviceFound(BluetoothDevice device, int rssi);
    }
}
