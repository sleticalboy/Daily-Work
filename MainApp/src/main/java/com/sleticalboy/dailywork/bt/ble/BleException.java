package com.sleticalboy.dailywork.bt.ble;

import android.util.AndroidException;

/**
 * Created on 20-8-19.
 *
 * @author Ben binli@grandstream.cn
 */
public class BleException extends AndroidException {

    public BleException(String msg) {
        super(msg);
    }

    public BleException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
