package com.binlee.sample.event;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.binlee.sample.AsyncCall;
import com.binlee.sample.IMessages;

/**
 * Created on 21-2-7.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ConnectEvent extends BluetoothGattCallback implements IEvent, AsyncCall {

    private final BluetoothDevice mTarget;
    @Type
    private final int mType;
    private boolean mFinished;
    private Context mContext;
    private Handler mHandler;
    private BluetoothGatt mGatt;
    private int mStatus;

    public ConnectEvent(BluetoothDevice target, @Type int type) {
        mTarget = target;
        mType = type;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public int type() {
        return mType;
    }

    @Override
    public BluetoothDevice target() {
        return mTarget;
    }

    @Override
    public void onFinish() {
        mFinished = true;
        release();
    }

    @Override
    public boolean isFinished() {
        return mFinished;
    }

    @Override
    public void run() {
        mFinished = false;
        int state = target().getBondState();
        if (state == BluetoothDevice.BOND_BONDED) {
            // notify GmdManager to care of this
            mHandler.obtainMessage(IMessages.BONDED_CHANGED, state, 0, target()).sendToTarget();
        } else if (state == BluetoothDevice.BOND_NONE) {
            // create bond
            mHandler.obtainMessage(IMessages.GATT_CREATE_BOND, target()).sendToTarget();
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (!target().equals(gatt.getDevice())) {
            return;
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            // status 8/19/22/133
            return;
        }
        if (newState != BluetoothProfile.STATE_CONNECTED) {
            return;
        }
        reportGattStatus(status);
        updateConnectStatus(STATUS_CONNECTING);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (!target().equals(gatt.getDevice())) {
            return;
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return;
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic bgc,
                                     int status) {
        if (!target().equals(gatt.getDevice())) {
            return;
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return;
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic bgc,
                                      int status) {
        if (!target().equals(gatt.getDevice())) {
            return;
        }
        if (status != BluetoothGatt.GATT_SUCCESS) {
            return;
        }
    }

    public void connectGatt() {
        mGatt = mTarget.connectGatt(mContext, false, this, BluetoothDevice.DEVICE_TYPE_LE);
    }

    private void updateConnectStatus(int status) {
        mStatus = status;
        Message msg = mHandler.obtainMessage(IMessages.CONNECT_STATUS_CHANGE, this);
        msg.arg1 = status;
        msg.sendToTarget();
    }

    private void reportGattStatus(int status) {
        Message msg = mHandler.obtainMessage(IMessages.GATT_STATUS_REPORTED, this);
        msg.arg1 = status;
        msg.sendToTarget();
    }

    private void release() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
        }
    }
}
