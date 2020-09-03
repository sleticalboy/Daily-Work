package com.sleticalboy.learning.bt.ble;

/**
 * Created on 20-8-13.
 *
 * @author Ben binli@grandstream.cn
 */
public interface IConnectCallback {

    void onFailure(Connection connection, BleException e);

    void onSuccess(Connection connection);
}
