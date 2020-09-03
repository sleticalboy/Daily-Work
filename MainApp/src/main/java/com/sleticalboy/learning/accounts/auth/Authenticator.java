package com.sleticalboy.learning.accounts.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.sleticalboy.learning.accounts.Constants;

/**
 * Created by AndroidStudio on 20-2-23.
 *
 * @author binlee
 */
public final class Authenticator extends AbstractAccountAuthenticator implements Constants {

    private static final String TAG = "Authenticator";
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
        // Log.d(TAG, "Authenticator() called with: context = [" + context + "]");
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "addAccount() called with: response = [" + response + "], accountType = ["
                + accountType + "], authTokenType = [" + authTokenType + "], options = [" + options + "]");
        if (options != null && options.keySet() != null) {
            for (final String key : options.keySet()) {
                Log.d(TAG, "addAccount: key = " + key + ", value = " + options.get(key));
            }
        }
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "getAuthToken() called with: response = [" + response + "], account = ["
                + account + "], authTokenType = [" + authTokenType + "], options = ["
                + options + "]");
        if (!ACCOUNT_AUTH_TOKEN.equals(authTokenType)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }
        final AccountManager am = AccountManager.get(mContext);
        final String password = am.getPassword(account);
        if (password != null) {
            // pretend requesting auth token.
            SystemClock.sleep(300L);
            final String authToken = ACCOUNT_AUTH_TOKEN;
            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }
        }
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(TAG, "editProperties() called with: response = [" + response + "], accountType = ["
                + accountType + "]");
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials() called with: response = [" + response + "], account = ["
                + account + "], options = [" + options + "]");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.d(TAG, "getAuthTokenLabel() called with: authTokenType = [" + authTokenType + "]");
        return "auto.token.label";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "updateCredentials() called with: response = [" + response + "], account = ["
                + account + "], authTokenType = [" + authTokenType + "], options = ["
                + options + "]");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        Log.d(TAG, "hasFeatures() called with: response = [" + response + "], account = ["
                + account + "], features = [" + features + "]");
        return null;
    }
}
