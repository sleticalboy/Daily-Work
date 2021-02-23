package com.binlee.sample.event;

import android.bluetooth.BluetoothDevice;

/**
 * Created on 21-2-7.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DisconnectEvent implements IEvent, AsyncCall {

    private final BluetoothDevice mTarget;
    @Type
    private final int mType;
    private boolean mFinished;

    public DisconnectEvent(BluetoothDevice target, @Type int type) {
        mTarget = target;
        mType = type;
    }

    @Override
    public BluetoothDevice target() {
        return mTarget;
    }

    @Override
    public void onFinish() {
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
        if (mType == CLICK_DISCONNECT) {
            //
        } else if (mType == UNBIND_DISCONNECT) {
            //
        } else if (mType == CONFIG_DISCONNECT) {
            //
        } else if (mType == OTHER_DISCONNECT) {
            //
        }
    }
}
