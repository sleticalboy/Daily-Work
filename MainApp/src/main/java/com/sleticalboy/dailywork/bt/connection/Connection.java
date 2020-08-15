package com.sleticalboy.dailywork.bt.connection;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;

import com.sleticalboy.dailywork.bt.BtUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created on 20-8-13.
 *
 * @author Ben binli@grandstream.cn
 */
public final class Connection extends BluetoothGattCallback implements Runnable {

    private static Method sConnectMethod;
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
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
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
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
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
        this.notifyAll();
    }

    void executeOn(ExecutorService service) {
        boolean success = false;
        try {
            service.execute(this);
            success = true;
            mCallback.onSuccess(this);
        } catch (RejectedExecutionException e) {
            mCallback.onFailure(this);
        } finally {
            if (!success) {
                mDispatcher.finish(this);
            }
        }
    }

    private void execute() {
        // 1、绑定蓝牙
        if (!mCanceled && !BtUtils.createBond(mDevice)) {
            // 发起绑定失败
            mCallback.onFailure(this);
            return;
        }
        while (!mCanceled && !isBonded(mDevice)) {
            try {
                wait(2000L);
            } catch (InterruptedException e) {
                // 绑定超时
                mCallback.onFailure(this);
                return;
            }
        }
        // 2、连接 profile
        final BluetoothProfile hidHost = mDispatcher.getHidHost();
        if (!mCanceled && !connectProfile(hidHost, mDevice)) {
            // 发起 profile 连接失败
            mCallback.onFailure(this);
            return;
        }
        while (!mCanceled && !isConnected(hidHost, mDevice)) {
            try {
                wait(2000L);
            } catch (InterruptedException e) {
                // profile 连接超时
                mCallback.onFailure(this);
                return;
            }
        }
        // 3、gatt 操作
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mDevice.connectGatt(mDispatcher.getContext(), false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            mDevice.connectGatt(mDispatcher.getContext(), false, this);
        }
    }

    private boolean isBonded(BluetoothDevice device) {
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    private boolean isConnected(BluetoothProfile proxy, BluetoothDevice device) {
        return proxy.getConnectionState(device) == BluetoothProfile.STATE_CONNECTED;
    }

    private boolean connectProfile(BluetoothProfile proxy, BluetoothDevice device) {
        if (sConnectMethod == null) {
            try {
                sConnectMethod = proxy.getClass().getDeclaredMethod("connect", device.getClass());
            } catch (NoSuchMethodException e) {
                return false;
            }
        } else {
            final Object obj;
            try {
                obj = sConnectMethod.invoke(proxy, device);
            } catch (IllegalAccessException | InvocationTargetException e) {
                return false;
            }
            return obj instanceof Boolean && (Boolean) obj;
        }
        return false;
    }
}
