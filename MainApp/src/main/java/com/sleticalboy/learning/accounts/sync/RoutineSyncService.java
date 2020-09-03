package com.sleticalboy.learning.accounts.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public final class RoutineSyncService extends Service {

    private static final Object LOCK = new Object();
    private static RoutineSyncAdapter sSyncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (LOCK) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new RoutineSyncAdapter(getApplicationContext());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
