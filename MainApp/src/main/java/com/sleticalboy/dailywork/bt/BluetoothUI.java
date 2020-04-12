package com.sleticalboy.dailywork.bt;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

import java.util.List;

public class BluetoothUI extends BaseActivity {

    private static final String TAG = "BluetoothUI";

    @Override
    protected int layoutResId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initView() {
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        findViewById(R.id.startScan).setOnClickListener(v -> {
            // if the bluetooth is disable, pop a dialog to enable it
            if (!manager.getAdapter().isEnabled()) {
                Log.d(TAG, "Bluetooth is disabled, enable it.");
                manager.getAdapter().enable();
            }
        });
        final BluetoothLeScanner scanner = manager.getAdapter().getBluetoothLeScanner();
        scanner.startScan(new ScanCallback() {
            @Override
            public void onScanResult(final int callbackType, final ScanResult result) {
                super.onScanResult(callbackType, result);
            }

            @Override
            public void onBatchScanResults(final List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(final int errorCode) {
                super.onScanFailed(errorCode);
            }
        });
    }
}
