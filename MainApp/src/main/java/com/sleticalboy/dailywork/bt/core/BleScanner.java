package com.sleticalboy.dailywork.bt.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

public class BleScanner {

    private final Handler mHandler;
    private final BluetoothAdapter mAdapter;
    private volatile boolean mStarted = false;
    private Object mRawCallback;

    public BleScanner(@SuppressWarnings("usuesd") Context context, Handler handler) {
        mHandler = handler;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScan(Callback callback) {
        startScan(callback, -1);
    }

    public void startScan(Callback callback, int duration) {
        if (mStarted) {
            return;
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback is null.");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ScanCallback rawCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    final ScanRecord record = result.getScanRecord();
                    if (record != null && callback.filter(record.getBytes())) {
                        callback.onScanResult(result.getDevice(), result.getRssi());
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    callback.onScanFailed(errorCode);
                }
            };
            mAdapter.getBluetoothLeScanner().startScan(rawCallback);
            mRawCallback = rawCallback;
        } else {
            final BluetoothAdapter.LeScanCallback rawCallback = (device, rssi, scanRecord) -> {
                if (callback.filter(scanRecord)) {
                    callback.onScanResult(device, rssi);
                }
            };
            mAdapter.startLeScan(rawCallback);
            mRawCallback = rawCallback;
        }
        mStarted = true;
        if (duration > 0) {
            mHandler.postDelayed(this::stopScan, duration);
        }
    }

    public void stopScan() {
        if (!mStarted) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAdapter.getBluetoothLeScanner().stopScan((ScanCallback) mRawCallback);
        } else {
            mAdapter.stopLeScan((BluetoothAdapter.LeScanCallback) mRawCallback);
        }
        mStarted = false;
    }

    public static class Callback {

        public void onScanResult(BluetoothDevice device, int rssi) {
        }

        public void onScanFailed(int errorCode) {
        }

        public boolean filter(byte[] scanRecord) {
            return true;
        }
    }
}
