package com.sleticalboy.learning.bt.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;
import android.util.Log;

import com.sleticalboy.learning.bt.BtUtils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created on 20-8-13.
 *
 * @author Ben binli@grandstream.cn
 */
public final class Connection extends BluetoothGattCallback implements Runnable {

    private static final String TAG = "Connection";

    private final BluetoothDevice mDevice;
    private IConnectCallback mCallback;
    private boolean mCanceled = false;
    private Dispatcher mDispatcher;

    public Connection(BluetoothDevice device, IConnectCallback callback) {
        mDevice = device;
        mCallback = callback;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.d(TAG, "onConnectionStateChange() status = [" + status + "], newState = [" + newState + "]");
        if (status == 0 && mDevice.equals(gatt.getDevice())) {
            final boolean started = gatt.discoverServices();
            Log.d(TAG, "onConnectionStateChange() -> start search services: " + started);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.d(TAG, "onServicesDiscovered() status = [" + status + "]");
        if (status == 0 && mDevice.equals(gatt.getDevice())) {
            Log.d(TAG, "onServicesDiscovered() start resolve all services...");
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic bgc, int status) {
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic bgc, int status) {
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic bgc) {
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor desc, int status) {
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor desc, int status) {
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        Log.d(TAG, "onReadRemoteRssi() rssi = [" + rssi + "], status = [" + status + "]");
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        Log.d(TAG, "onMtuChanged() mtu = [" + mtu + "], status = [" + status + "]");
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(mDevice.getAddress());
        try {
            execute();
        } finally {
            Thread.currentThread().setName(oldName);
        }
    }

    public void cancel() {
        mCallback = null;
        mCanceled = true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Connection)) return false;
        final Connection that = (Connection) o;
        return mDevice.equals(that.mDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDevice);
    }

    void setDispatcher(Dispatcher dispatcher) {
        mDispatcher = dispatcher;
    }

    BluetoothDevice getDevice() {
        return mDevice;
    }

    public void notifyStateChange() {
        synchronized (this) {
            notifyAll();
        }
    }

    void executeOn(ExecutorService service) {
        boolean success = false;
        try {
            service.execute(this);
            success = true;
            mCallback.onSuccess(this);
        } catch (RejectedExecutionException e) {
            mCallback.onFailure(this, new BleException("", e));
        } finally {
            if (!success) {
                mDispatcher.finish(this);
            }
        }
    }

    private void execute() {
        // 1、绑定蓝牙
        if (mCanceled) {
            mCallback.onFailure(this, new BleException("Canceled."));
            return;
        }
        if (!BtUtils.createBond(mDevice)) {
            // 发起绑定失败
            mCallback.onFailure(this, new BleException("Create bond failed."));
            return;
        } else {
            try {
                // java.lang.IllegalMonitorStateException: object not locked by thread before wait()
                synchronized (this) {
                    wait(2000L);
                }
            } catch (InterruptedException e) {
                // 绑定超时
                mCallback.onFailure(this, new BleException("Bond timeout.", e));
                return;
            }
        }
        // 2、gatt 操作, 回调方法默认是在主线程执行的，请勿执行耗时操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDevice.connectGatt(mDispatcher.getContext(), false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            mDevice.connectGatt(mDispatcher.getContext(), false, this);
        }
        // BtUtils.isConnected(device)
    }
}
