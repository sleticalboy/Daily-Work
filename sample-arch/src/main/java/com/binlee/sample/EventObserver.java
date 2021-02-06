package com.binlee.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created on 21-2-6.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class EventObserver extends BroadcastReceiver implements IComponent {

    private final Context mContext;
    private Intent mIntent;

    public EventObserver(Context context) {
        mContext = context;
    }

    @Override
    public void onStart() {
        startObserve();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
    }

    @Override
    public void onDestroy() {
        stopObserve();
    }

    private void startObserve() {
        IntentFilter filter = new IntentFilter();
        mIntent = mContext.registerReceiver(this, filter);
    }

    private void stopObserve() {
        if (mIntent != null) {
            mContext.unregisterReceiver(this);
        }
    }
}
