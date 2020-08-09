package com.sleticalboy.dailywork.bt.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

public class BleScanner {

    private static final String TAG = "BleScanner";

    private final Handler mHandler;
    private final BluetoothAdapter mAdapter;
    private volatile boolean mStarted = false;
    private Object mRawCallback;

    public BleScanner(@SuppressWarnings("usuesd") Context context, Handler handler) {
        mHandler = handler;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScan(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("request is null.");
        }
        if (mStarted || !mAdapter.isEnabled()) {
            if (!mStarted) {
                Log.d(TAG, "Bluetooth is disabled, enable it.");
                mAdapter.enable();
                mHandler.postDelayed(() -> startScan(request), 500L);
            }
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final ScanCallback rawCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Log.d(TAG, "onScanResult() called with: callbackType = [" + callbackType + "], result = [" + result + "]");
                    final ScanRecord record = result.getScanRecord();
                    if (record != null && request.mCallback.filter(record.getBytes())) {
                        boolean connectable = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            connectable = result.isConnectable();
                        }
                        final Result rst = Result.obtain();
                        rst.mDevice = result.getDevice();
                        rst.mRssi = result.getRssi();
                        rst.mConnectable = connectable;
                        request.mCallback.onScanResult(rst);
                        rst.recycle();
                    }
                }

                @Override
                public void onScanFailed(int errorCode) {
                    request.mCallback.onScanFailed(errorCode);
                }
            };
//            final ScanFilter filter = new ScanFilter.Builder().build();
//            final ScanSettings settings = new ScanSettings.Builder()
//                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                    .build();
            mAdapter.getBluetoothLeScanner().startScan(rawCallback);
            mRawCallback = rawCallback;
        } else {
            final BluetoothAdapter.LeScanCallback rawCallback = (device, rssi, scanRecord) -> {
                if (request.mCallback.filter(scanRecord)) {
                    final Result rst = Result.obtain();
                    rst.mDevice = device;
                    rst.mRssi = rssi;
                    rst.mConnectable = true;
                    request.mCallback.onScanResult(rst);
                    rst.recycle();
                }
            };
            mAdapter.startLeScan(rawCallback);
            mRawCallback = rawCallback;
        }
        mStarted = true;
        if (request.mDuration > 0) {
            mHandler.postDelayed(this::stopScan, request.mDuration);
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

        public void onScanResult(Result result) {
        }

        public void onScanFailed(int errorCode) {
        }

        public boolean filter(byte[] scanRecord) {
            return true;
        }
    }

    public static class Request {
        @NonNull
        public Callback mCallback;
        public long mDuration;
    }

    public static class Result {

        public BluetoothDevice mDevice;
        public int mRssi;
        public boolean mConnectable = true;

        private static Result sPool;
        private static int sPoolSize;
        private static final Object POOL_LOCK = new Object();
        private Result mNext;
        private int mInUse;

        private Result() {
        }

        @Override
        public String toString() {
            return "{device=" + mDevice + ", rssi=" + mRssi + ", connectable=" + mConnectable + '}';
        }

        public static Result obtain() {
            synchronized (POOL_LOCK) {
                if (sPool != null) {
                    final Result r = sPool;
                    sPool = r.mNext;
                    r.mNext = null;
                    r.mInUse = 0;
                    sPoolSize--;
                    return r;
                }
            }
            return new Result();
        }

        public void recycle() {
            if (mInUse == 0) {
                return;
            }
            mDevice = null;
            mRssi = 0;
            mConnectable = true;
            mInUse = 0;
            synchronized (POOL_LOCK) {
                if (sPoolSize < 15) {
                    mNext = sPool;
                    sPool = this;
                    sPoolSize++;
                }
            }
        }
    }
}
