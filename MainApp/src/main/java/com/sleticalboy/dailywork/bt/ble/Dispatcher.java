package com.sleticalboy.dailywork.bt.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 20-8-13.
 *
 * @author Ben binli@grandstream.cn
 */
public final class Dispatcher {

    private final Deque<Connection> mReadyConns = new ArrayDeque<>();
    private final Deque<Connection> mRunningConns = new ArrayDeque<>();

    private int mMaxRequest = 5;
    private ExecutorService mExecutorService;
    private BluetoothProfile mHidHost;
    private final Context mContext;

    public Dispatcher(Context context) {
        mContext = context;
    }

    private void promoteAndExecute() {
        final List<Connection> executables = new ArrayList<>();
        synchronized (this) {
            for (Iterator<Connection> it = mReadyConns.iterator(); it.hasNext(); ) {
                final Connection conn = it.next();
                if (mRunningConns.size() >= mMaxRequest) {
                    break;
                }
                it.remove();
                executables.add(conn);
                mRunningConns.add(conn);
            }
        }
        for (Connection conn : executables) {
            conn.executeOn(executorService());
        }
    }

    private ExecutorService executorService() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(0, 60, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<>(), r -> {
                final Thread thread = new Thread(r, "Ble Dispatcher");
                thread.setDaemon(false);
                return thread;
            });
        }
        return mExecutorService;
    }

    void finish(Connection connection) {
        promoteAndExecute();
    }

    Context getContext() {
        return mContext;
    }

    void notifyConnectionState(BluetoothDevice device) {
        for (final Connection conn : mRunningConns) {
            if (Objects.equals(device, conn.getDevice())) {
                conn.notifyStateChange();
            }
        }
    }

    public void setHidHost(BluetoothProfile hidHost) {
        mHidHost = hidHost;
    }

    public BluetoothProfile getHidHost() {
        return mHidHost;
    }

    public void setMaxRequest(final int maxRequest) {
        mMaxRequest = maxRequest;
        promoteAndExecute();
    }

    public void enqueue(Connection connection) {
        connection.setDispatcher(this);
        synchronized (this) {
            mReadyConns.add(connection);
        }
        promoteAndExecute();
    }

    public void cancel(BluetoothDevice device) {
        for (final Connection conn : mReadyConns) {
            if (Objects.equals(device, conn.getDevice())) {
                conn.cancel();
            }
        }
        for (final Connection conn : mRunningConns) {
            if (Objects.equals(device, conn.getDevice())) {
                conn.cancel();
            }
        }
    }

    public void cancelAll() {
        for (Connection connection : mReadyConns) {
            connection.cancel();
        }
        for (Connection connection : mRunningConns) {
            connection.cancel();
        }
    }
}
