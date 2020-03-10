package com.sleticalboy.dailywork.accounts.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created by AndroidStudio on 20-2-23.
 *
 * @author binlee
 */
public final class ContactsSyncService extends Service {

    private static final Object LOCK = new Object();
    private static ContactsSyncAdapter sSyncAdapter;

    @Override
    public void onCreate() {
        synchronized (LOCK) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new ContactsSyncAdapter(getApplicationContext());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
