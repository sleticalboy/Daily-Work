package com.binlee.sample.event;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface AsyncCall extends Runnable, IEvent {

    // connect status
    int STATUS_CONNECTED = 0x01;
    int STATUS_NOT_CONNECTED = 0x02;
    int STATUS_CONNECTING = 0x03;
    int STATUS_DISCONNECTING = 0x04;
    int STATUS_CONFIG_START = 0x05;
    int STATUS_CONFIG_SECOND = 0x06;
    int STATUS_CONFIG_OVER = 0x07;

    // finish reason
    int REASON_CONNECT_DONE = 0x01;
    int REASON_DISCONNECT_DONE = 0x02;
    int REASON_TIMEOUT = 0x03;
    int REASON_FAILED = 0x04;
    int REASON_ABORTED = 0x05;

    BluetoothDevice target();

    void onFinish();

    void onFinish(int reason);

    boolean isFinished();

    @Override
    void run();

    default void setContext(Context context) {
    }

    default void setHandler(Handler handler) {
    }

    default String getStatus(int status) {
        String format;
        switch (status) {
            default:
                format = "Unknown status(%02x)";
                break;
            case STATUS_CONNECTED:
                format = "STATUS_CONNECTED(%02x)";
                break;
            case STATUS_NOT_CONNECTED:
                format = "STATUS_NOT_CONNECTED(%02x)";
                break;
            case STATUS_CONNECTING:
                format = "STATUS_CONNECTING(%02x)";
                break;
            case STATUS_DISCONNECTING:
                format = "STATUS_DISCONNECTING(%02x)";
                break;
            case STATUS_CONFIG_START:
                format = "STATUS_CONFIG_START(%02x)";
                break;
            case STATUS_CONFIG_OVER:
                format = "STATUS_CONFIG_OVER(%02x)";
                break;
        }
        return String.format(format, status);
    }

    default String getReason(int reason) {
        String format;
        switch (reason) {
            default:
                format = "Unknown reason(%02x)";
                break;
            case REASON_CONNECT_DONE:
                format = "REASON_CONNECT_DONE(%02x)";
                break;
            case REASON_DISCONNECT_DONE:
                format = "REASON_DISCONNECT_DONE(%02x)";
                break;
            case REASON_TIMEOUT:
                format = "REASON_TIMEOUT(%02x)";
                break;
            case REASON_FAILED:
                format = "REASON_FAILED(%02x)";
                break;
            case REASON_ABORTED:
                format = "REASON_ABORTED(%02x)";
                break;
        }
        return String.format(format, reason);
    }
}
