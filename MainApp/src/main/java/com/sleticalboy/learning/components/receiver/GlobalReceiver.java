package com.sleticalboy.learning.components.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GlobalReceiver extends BroadcastReceiver {

    private static final String TAG = "GlobalReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)
                || Intent.ACTION_USER_PRESENT.equals(action)) {
            Log.d(TAG, "onReceive() receive: " + action + ", start work.");
        }
    }
}
