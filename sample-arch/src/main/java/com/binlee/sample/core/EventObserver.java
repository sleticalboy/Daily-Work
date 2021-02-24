package com.binlee.sample.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.binlee.sample.core.IComponent;
import com.binlee.sample.core.IWhat;

/**
 * Created on 21-2-6.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class EventObserver extends BroadcastReceiver implements IComponent {

    private final Context mContext;
    private final Handler mCallback;
    private Intent mIntent;

    public EventObserver(Context context, Handler callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onStart() {
        startObserve();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED".equals(action)) {
            Message msg = mCallback.obtainMessage(IWhat.HID_PROFILE_CHANGED);
            msg.obj = intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME);
            msg.arg1 = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
            msg.sendToTarget();
        } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            mCallback.obtainMessage(IWhat.LOCALE_CHANGED).sendToTarget();
        }
    }

    @Override
    public void onDestroy() {
        stopObserve();
    }

    private void startObserve() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        mIntent = mContext.registerReceiver(this, filter);
    }

    private void stopObserve() {
        if (mIntent != null) {
            mContext.unregisterReceiver(this);
        }
    }
}
