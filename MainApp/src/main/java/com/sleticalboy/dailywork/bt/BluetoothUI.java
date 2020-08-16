package com.sleticalboy.dailywork.bt;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.bt.ble.Connection;
import com.sleticalboy.dailywork.bt.ble.IConnectCallback;
import com.sleticalboy.dailywork.bt.ble.BleScanner;
import com.sleticalboy.dailywork.bt.ble.BleService;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUI extends BaseActivity {

    private BleService.LeBinder mService;
    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(getTag(), "onServiceConnected() name: " + name + ", service: " + service);
            mService = ((BleService.LeBinder) service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(getTag(), "onServiceDisconnected() name:" + name);
            mService = null;
        }
    };
    private DevicesAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        final Intent intent = new Intent(this, BleService.class);
        bindService(intent, mConn, BIND_AUTO_CREATE);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected String getTag() {
        return "BluetoothUI";
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_bluetooth;
    }

    @NonNull
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

        RecyclerView mDevicesRv = findViewById(R.id.btDevicesRv);
        mDevicesRv.setLayoutManager(new LinearLayoutManager(this));
        mDevicesRv.setAdapter(mAdapter = new DevicesAdapter());
    }

    private void startBtScan() {
        if (mService == null) {
            return;
        }
        final BleScanner.Request request = new BleScanner.Request();
        request.mCallback = new BleScanner.Callback() {
            @Override
            public void onScanResult(BleScanner.Result result) {
                // Log.d(getTag(), "onDeviceScanned() " + result);
                mAdapter.addDevice(result);
            }

            @Override
            public void onScanFailed(int errorCode) {
            }
        };
        request.mDuration = 10000L;
        mService.startScan(request);
    }

    private void stopBtScan() {
        if (mService != null) {
            mService.stopScan();
        }
    }

    private void doConnect(BluetoothDevice device) {
        Log.d(getTag(), "connect to " + device);
        if (mService != null) {
            mService.connect(device, new IConnectCallback() {
                @Override
                public void onFailure(Connection connection) {
                    //
                }

                @Override
                public void onSuccess(Connection connection) {
                    //
                }
            });
        }
    }

    private void doCancel(BluetoothDevice device) {
        if (mService != null) {
            mService.cancel(device);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBtScan();
        doCancel(null);
        unbindService(mConn);
    }

    private final class DevicesAdapter extends RecyclerView.Adapter<DeviceHolder> {

        private final List<BleScanner.Result> mDataSet = new ArrayList<>();

        private final List<BleScanner.Result> mDataCopy = new ArrayList<>();

        @NonNull
        @Override
        public DeviceHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            return new DeviceHolder(getLayoutInflater().inflate(
                    R.layout.item_ble_recycler, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(@NonNull final DeviceHolder holder, final int position) {
            final BleScanner.Result result = mDataSet.get(position);
            holder.tvName.setText(Html.fromHtml("<font color='red'>" + result.mDevice.getName()
                    + "</font>  <font color='blue'>" + result.mDevice.getAddress()));
            holder.btnConnect.setEnabled(result.mConnectable);
            holder.btnConnect.setOnClickListener(v -> doConnect(result.mDevice));
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public List<BleScanner.Result> getData() {
            mDataCopy.clear();
            mDataCopy.addAll(mDataSet);
            return mDataCopy;
        }
        public void addDevice(BleScanner.Result result) {
            final int index = mDataSet.indexOf(result);
            if (index < 0) {
                mDataSet.add(result);
            } else {
                mDataSet.set(index, result);
            }
            Log.d(getTag(), "onDeviceScanned() index: " + index + ", device: " + result);
            notifyItemChanged(index < 0 ? mDataSet.size() - 1 : index);
        }

    }

    private static final class DeviceHolder extends RecyclerView.ViewHolder {

        final TextView tvName;
        final TextView btnConnect;

        public DeviceHolder(@NonNull final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ble_name);
            btnConnect = itemView.findViewById(R.id.btn_connect);
        }
    }
}
