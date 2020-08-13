package com.sleticalboy.dailywork.bt.connection;

/**
 * Created on 20-8-13.
 *
 * @author Ben binli@grandstream.cn
 */
public interface IConnectCallback {

    void onFailure(Connection connection);

    void onSuccess(Connection connection);
}
