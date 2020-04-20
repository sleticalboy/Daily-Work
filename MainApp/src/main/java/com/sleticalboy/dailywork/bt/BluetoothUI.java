package com.sleticalboy.dailywork.bt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
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

import java.util.ArrayList;
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
            // final BluetoothLeScanner scanner = manager.getAdapter().getBluetoothLeScanner();
            if (mScanCallback == null) {
                mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(final int callbackType, final ScanResult result) {
                        Log.d(TAG, "onScanResult() called with: callbackType = [" + callbackType + "], result = [" + result + "]");
                        ThreadHelper.runOnMain(() -> onDeviceScanned(result));
                    }

                    @Override
                    public void onBatchScanResults(final List<ScanResult> results) {
                        Log.d(TAG, "onBatchScanResults() called with: results = [" + results + "]");
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
            manager.getAdapter().getBluetoothLeScanner().startScan(mScanCallback);
        } else {
            if (mLeScanCallback == null) {
                mLeScanCallback = (device, rssi, scanRecord) -> {
                    Log.d(TAG, "onLeScan() called with: device = [" + device + "], rssi = [" + rssi + "], scanRecord = [" + scanRecord + "]");
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
        final BluetoothDevice device = result.getDevice();
        Log.d(TAG, "onDeviceScanned() called with: result = [" + result + "]");
        mAdapter.addDevice(device);
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
            holder.mBt.setText(device.getName() + " " + device.getAddress());
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
            notifyItemChanged(index < 0 ? mDataSet.size() - 1 : index);
        }
    }

    private static final class DeviceHolder extends RecyclerView.ViewHolder {

        final TextView mBt;

        public DeviceHolder(@NonNull final View itemView) {
            super(itemView);
            mBt = ((TextView) itemView);
        }
    }
}
