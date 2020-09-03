package com.sleticalboy.learning.accounts.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public final class RoutineSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "RoutineSyncAdapter";

    RoutineSyncAdapter(final Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() called with: account = [" + account + "], extras = [" + extras
                + "], authority = [" + authority + "], provider = [" + provider
                + "], syncResult = [" + syncResult + "], " + Thread.currentThread());
        SystemClock.sleep(800L);
    }
}
