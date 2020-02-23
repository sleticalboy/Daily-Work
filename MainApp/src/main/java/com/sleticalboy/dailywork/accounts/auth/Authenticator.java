package com.sleticalboy.dailywork.accounts.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by AndroidStudio on 20-2-23.
 *
 * @author binlee
 */
public final class Authenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "Authenticator";

    public Authenticator(Context context) {
        super(context);
        Log.d(TAG, "Authenticator() called with: context = [" + context + "]");
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(TAG, "editProperties() called with: response = [" + response + "], accountType = ["
                + accountType + "]");
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "addAccount() called with: response = [" + response + "], accountType = ["
                + accountType + "], authTokenType = [" + authTokenType + "], requiredFeatures = ["
                + requiredFeatures + "], options = [" + options + "]");
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
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options)
            throws NetworkErrorException {
        Log.d(TAG, "getAuthToken() called with: response = [" + response + "], account = ["
                + account + "], authTokenType = [" + authTokenType + "], options = ["
                + options + "]");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.d(TAG, "getAuthTokenLabel() called with: authTokenType = [" + authTokenType + "]");
        return null;
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
