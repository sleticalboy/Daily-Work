package com.sleticalboy.dailywork.components.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sleticalboy.dailywork.components.service.MyService;

public class GlobalReceiver extends BroadcastReceiver {

    private static final String TAG = "GlobalReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive() called with: action: " + intent.getAction());
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())
                || Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            context.startService(new Intent(context, MyService.class));
        }
    }
}
