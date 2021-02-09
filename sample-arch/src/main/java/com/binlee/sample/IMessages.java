package com.binlee.sample;

/**
 * Created on 21-2-7.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IMessages {

    int GATT_STATUS_REPORTED = 0x01;
    int CONNECT_STATUS_CHANGE = 0x02;
    int GATT_CREATE_BOND = 0x03;

    int STOP_SCAN = 0x04;
    int RESUME_SCAN = 0x05;
    int SCAN_RESULT = 0x06;
    int SCAN_FAILED = 0x07;

    int BONDED_CHANGED = 0x08;
    int HID_PROFILE_CHANGED = 0x09;

    int LOCALE_CHANGED = 0x0a;
}
