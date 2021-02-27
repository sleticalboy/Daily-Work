package com.binlee.sample.model;

import android.bluetooth.BluetoothDevice;

/**
 * Created on 21-2-27.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ArchDevice {

    private final BluetoothDevice mTarget;

    public ArchDevice(BluetoothDevice target) {
        mTarget = target;
    }

    public BluetoothDevice target() {
        return mTarget;
    }
}
