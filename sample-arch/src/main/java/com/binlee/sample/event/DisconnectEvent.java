package com.binlee.sample.event;

import android.bluetooth.BluetoothDevice;

/**
 * Created on 21-2-7.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DisconnectEvent implements AsyncEvent {

    private final BluetoothDevice mTarget;
    @Type
    private final int mType;
    private boolean mFinished;
    private int mStatus;

    public DisconnectEvent(BluetoothDevice target, @Type int type) {
        mTarget = target;
        mType = type;
    }

    @Override
    public BluetoothDevice target() {
        return mTarget;
    }

    @Override
    public void onFinish(int reason) {
        mFinished = true;
    }

    @Override
    public boolean isFinished() {
        return mFinished;
    }

    @Override
    public int type() {
        return mType;
    }

    @Override
    public void run() {
        mFinished = true;
        disconnectNrf();
        mStatus = mStatus == STATUS_CONNECTED ? STATUS_DISCONNECTING : STATUS_NOT_CONNECTED;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    private void disconnectNrf() {
        //
    }
}
