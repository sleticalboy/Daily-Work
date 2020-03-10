package com.sleticalboy.dailywork.accounts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.sleticalboy.dailywork.accounts.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by AndroidStudio on 20-2-23.
 *
 * @author binlee
 */
final class ContactsSyncAdapter extends AbstractThreadedSyncAdapter implements Constants {

    private static final String TAG = "ContactsSyncAdapter";
    private static final String SYNC_MARKER_KEY = "com.sleticalboy.sync.marker";
    private static final boolean NOTIFY_AUTH_FAILURE = true;
    private final AccountManager mAccountManager;

    ContactsSyncAdapter(Context context) {
        super(context, true);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() called with: account = [" + account + "], extras = [" + extras
                + "], authority = [" + authority + "], provider = [" + provider
                + "], syncResult = [" + syncResult + "], " + Thread.currentThread());
        printObj(extras);
        SystemClock.sleep(800L);
        try {
            final long lastSyncMarker = getServerSyncMarker(account);
            if (lastSyncMarker == 0) {
                setAccountContactsVisibility(getContext(), account, true);
            }
            final String authToken = mAccountManager.blockingGetAuthToken(account,
                    ACCOUNT_TYPE, NOTIFY_AUTH_FAILURE);
            Log.d(TAG, "onPerformSync: authToken = " + authToken);
        } catch (Throwable e) {
            if (e instanceof AuthenticatorException) {
                syncResult.stats.numAuthExceptions++;
            } else if (e instanceof IOException) {
                syncResult.stats.numIoExceptions++;
            } else if (e instanceof IllegalAccessException) {
                syncResult.databaseError = true;
            } else {
                syncResult.stats.numParseExceptions++;
            }
            e.printStackTrace();
        }
    }

    public static void printObj(Object obj) {
        if (obj == null) {
            Log.d(TAG, "printObj: obj is null");
        }
        if (obj instanceof Map) {
            Log.d(TAG, "printObj: obj is " + obj);
        } else if (obj instanceof List) {
            Log.d(TAG, "printObj: obj is " + obj);
        } else if (obj instanceof Bundle) {
            printBundle(((Bundle) obj), "Bundle");
        } else if (obj instanceof Intent) {
            printBundle(((Intent) obj).getExtras(), "Intent bundle");
        } else if (obj instanceof Throwable) {
            Log.d(TAG, "printObj: obj is Throwable", ((Throwable) obj));
        } else {
            Log.d(TAG, "printObj: obj is " + obj);
        }
    }

    private static void printBundle(Bundle bundle, String prefix) {
        if (bundle == null || bundle.isEmpty()) {
            Log.d(TAG, prefix + " empty bundle.");
            return;
        }
        for (final String key : bundle.keySet()) {
            final Object value = bundle.get(key);
            if (value instanceof Bundle) {
                printBundle(((Bundle) value), "Bundle");
            } else if (value instanceof Intent) {
                printBundle(((Intent) value).getExtras(), "Intent bundle");
            } else {
                Log.d(TAG, prefix + " key = " + value);
            }
        }
    }

    public static void setAccountContactsVisibility(Context context, Account account,
                                                    boolean visible) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, account.name);
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE);
        values.put(ContactsContract.Settings.UNGROUPED_VISIBLE, visible ? 1 : 0);
        context.getContentResolver().insert(DB_URI, values);
    }

    private long getServerSyncMarker(Account account) {
        String markerString = mAccountManager.getUserData(account, SYNC_MARKER_KEY);
        if (!TextUtils.isEmpty(markerString)) {
            return Long.parseLong(markerString);
        }
        return 0;
    }

    /**
     * Save off the high-water-mark we receive back from the server.
     *
     * @param account The account we're syncing
     * @param marker  The high-water-mark we want to save.
     */
    private void setServerSyncMarker(Account account, long marker) {
        mAccountManager.setUserData(account, SYNC_MARKER_KEY, Long.toString(marker));
    }
}
