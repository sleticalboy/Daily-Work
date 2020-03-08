package com.sleticalboy.dailywork.accounts.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.sleticalboy.dailywork.accounts.Constants;

import java.io.IOException;

/**
 * Created by AndroidStudio on 20-2-23.
 *
 * @author binlee
 */
final class SyncAdapter extends AbstractThreadedSyncAdapter implements Constants {

    private static final String TAG = "SyncAdapter";
    private static final String SYNC_MARKER_KEY = "com.sleticalboy.sync.marker";
    private static final boolean NOTIFY_AUTH_FAILURE = true;
    private final AccountManager mAccountManager;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync() called with: account = [" + account + "], extras = [" + extras
                + "], authority = [" + authority + "], provider = [" + provider
                + "], syncResult = [" + syncResult + "]");
        try {
            final long lastSyncMarker = getServerSyncMarker(account);
            if (lastSyncMarker == 0) {
                setAccountContactsVisibility(getContext(), account, true);
            }
            final String authToken = mAccountManager.blockingGetAuthToken(account,
                    ACCOUNT_TYPE, NOTIFY_AUTH_FAILURE);
            Log.d(TAG, "onPerformSync: authToken = " + authToken);
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
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
