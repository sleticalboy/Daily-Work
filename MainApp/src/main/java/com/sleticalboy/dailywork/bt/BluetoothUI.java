package com.sleticalboy.dailywork.bt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.util.ThreadHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BluetoothUI extends BaseActivity {

    private static final String TAG = "BluetoothUI";
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private ScanCallback mScanCallback;
    private RecyclerView mDevicesRv;
    private DevicesAdapter mAdapter;

    @Override
    protected int layoutResId() {
        return R.layout.activity_bluetooth;
    }

    @NotNull
    @Override
    protected String[] requiredPermissions() {
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == getRequestCode()) {
            //
        }
    }

    @Override
    protected void initView() {
        findViewById(R.id.startScan).setOnClickListener(v -> startBtScan());
        findViewById(R.id.stopScan).setOnClickListener(v -> stopBtScan());

        mDevicesRv = findViewById(R.id.btDevicesRv);
        mDevicesRv.setLayoutManager(new LinearLayoutManager(this));
        mDevicesRv.setAdapter(mAdapter = new DevicesAdapter());
    }

    private void startBtScan() {
        final BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        // if the bluetooth is disable, pop a dialog to enable it
        if (!manager.getAdapter().isEnabled()) {
            Log.d(TAG, "Bluetooth is disabled, enable it.");
            manager.getAdapter().enable();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (mScanCallback == null) {
                mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(final int callbackType, final ScanResult result) {
                        // Log.d(TAG, "onScanResult() called with: callbackType: " + callbackType
                        //         + " " + Thread.currentThread());
                        ThreadHelper.runOnMain(() -> onDeviceScanned(result));
                    }

                    @Override
                    public void onBatchScanResults(final List<ScanResult> results) {
                        // Log.d(TAG, "onBatchScanResults() thread: " + Thread.currentThread());
                        if (results != null) {
                            for (final ScanResult result : results) {
                                ThreadHelper.runOnMain(() -> onDeviceScanned(result));
                            }
                        }
                    }

                    @Override
                    public void onScanFailed(final int errorCode) {
                        Log.d(TAG, "onScanFailed() called with: errorCode = [" + errorCode + "]");
                    }
                };
            }
            final ScanFilter filter = new ScanFilter.Builder().build();
            final ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            manager.getAdapter().getBluetoothLeScanner()
                    .startScan(Collections.singletonList(filter), settings, mScanCallback);
        } else {
            if (mLeScanCallback == null) {
                mLeScanCallback = (device, rssi, scanRecord) -> {
                    Log.d(TAG, "onLeScan()  rssi: " + rssi + ", thread: " + Thread.currentThread());
                    mAdapter.addDevice(device);
                };
            }
            manager.getAdapter().startLeScan(mLeScanCallback);
        }
    }

    private void onDeviceScanned(final ScanResult result) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                || result == null || result.getDevice() == null) {
            return;
        }
        mAdapter.addDevice(result.getDevice());
    }

    private void stopBtScan() {
        final BluetoothManager manager = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE));
        if (!manager.getAdapter().isEnabled()) {
            Log.d(TAG, "stopBtScan() returned as bt adapter is disabled.");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.getAdapter().getBluetoothLeScanner().stopScan(mScanCallback);
        } else {
            manager.getAdapter().stopLeScan(mLeScanCallback);
        }
    }

    private final class DevicesAdapter extends RecyclerView.Adapter<DeviceHolder> {

        private final List<BluetoothDevice> mDataSet = new ArrayList<>();
        private final List<BluetoothDevice> mDataCopy = new ArrayList<>();

        @NonNull
        @Override
        public DeviceHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            return new DeviceHolder(getLayoutInflater().inflate(
                    R.layout.item_recycler, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final DeviceHolder holder, final int position) {
            final BluetoothDevice device = mDataSet.get(position);
            holder.mBt.setText(Html.fromHtml("<font color='red'>" + device.getName()
                    + "</font>  <font color='blue'>" + device.getAddress()));
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public List<BluetoothDevice> getData() {
            mDataCopy.clear();
            mDataCopy.addAll(mDataSet);
            return mDataCopy;
        }

        public void addDevice(BluetoothDevice device) {
            final int index = mDataSet.indexOf(device);
            if (index < 0) {
                mDataSet.add(device);
            } else {
                mDataSet.set(index, device);
            }
            Log.d(TAG, "onDeviceScanned() index: " + index + ", device: " + device);
            notifyItemChanged(index < 0 ? mDataSet.size() - 1 : index);
        }
    }

    private static final class DeviceHolder extends RecyclerView.ViewHolder {

        final TextView mBt;

        public DeviceHolder(@NonNull final View itemView) {
            super(itemView);
            mBt = ((TextView) itemView);
            mBt.setTextSize(36);
        }
    }
}
