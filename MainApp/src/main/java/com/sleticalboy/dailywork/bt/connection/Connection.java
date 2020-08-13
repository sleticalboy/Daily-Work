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

    void setDispatcher(Dispatcher dispatcher) {
        mDispatcher = dispatcher;
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
        if (!BtUtils.createBond(mDevice)) {
            // 发起绑定失败
            mCallback.onFailure(this);
            return;
        }
        while (mDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
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
        if (!connectProfile(hidHost, mDevice)) {
            // 发起 profile 连接失败
            mCallback.onFailure(this);
            return;
        }
        while (getConnectionState(hidHost, mDevice) != BluetoothProfile.STATE_CONNECTED) {
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
            mDevice.connectGatt(mDispatcher.context(), false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            mDevice.connectGatt(mDispatcher.context(), false, this);
        }
    }

    private int getConnectionState(BluetoothProfile proxy, BluetoothDevice device) {
        if (proxy != null) {
            return proxy.getConnectionState(device);
        }
        return BluetoothProfile.STATE_DISCONNECTED;
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

    public void cancel() {
        mCallback = null;
        mCanceled = true;
    }
}
