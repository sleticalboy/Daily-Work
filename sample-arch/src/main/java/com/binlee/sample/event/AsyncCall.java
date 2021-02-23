package com.binlee.sample.event;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface AsyncCall extends Runnable {

    int STATUS_CONNECTED = 0x01;
    int STATUS_NOT_CONNECTED = 0x02;
    int STATUS_CONNECTING = 0x03;
    int STATUS_DISCONNECTING = 0x04;
    int STATUS_CONFIG_START = 0x05;
    int STATUS_CONFIG_SECOND = 0x06;
    int STATUS_CONFIG_OVER = 0x06;

    BluetoothDevice target();

    void onFinish();

    boolean isFinished();

    @Override
    void run();

    default void setContext(Context context) {
    }

    default void setHandler(Handler handler) {
    }
}
