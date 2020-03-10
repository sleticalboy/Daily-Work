package com.sleticalboy.dailywork.accounts.auth;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.accounts.Constants;
import com.sleticalboy.dailywork.base.BaseActivity;

import org.jetbrains.annotations.Nullable;

public class AuthenticatorActivity extends BaseActivity implements Constants {

    private static final String TAG = "AuthenticatorActivity";

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse;
    private Bundle mResultBundle;
    private String mUsername, mPassword;
    private AccountManager mAccountManager;
    private boolean mRequestNewAccount = false;
    private ProgressBar mLoading;

    @Override
    protected void prepareWork(@Nullable final Bundle savedInstanceState) {
        mAccountManager = AccountManager.get(getApplication());
        mUsername = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        mRequestNewAccount = mUsername == null;
        mAccountAuthenticatorResponse = getIntent().getParcelableExtra(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }

    @Override
    public void initView() {
        mLoading = findViewById(R.id.loading);
        final Button loginBtn = findViewById(R.id.login);
        final EditText usernameEt = findViewById(R.id.username);
        final EditText passwordEt = findViewById(R.id.password);
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start,
                                          final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start,
                                      final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                loginBtn.setEnabled(!TextUtils.isEmpty(usernameEt.getText())
                        && !TextUtils.isEmpty(passwordEt.getText()));
            }
        };
        usernameEt.addTextChangedListener(watcher);
        passwordEt.addTextChangedListener(watcher);
        loginBtn.setOnClickListener(v -> {
            mLoading.setVisibility(View.VISIBLE);
            mUsername = usernameEt.getText().toString().trim();
            mPassword = passwordEt.getText().toString().trim();
            handleLogin(v);
        });
    }

    private void handleLogin(final View v) {
        if (mUsername.startsWith(ACCOUNT_PREFIX) && mPassword.startsWith(ACCOUNT_PASSWORD)) {
            v.postDelayed(() -> finishLogin(ACCOUNT_AUTH_TOKEN), 300L);
        } else {
            finishLogin(null);
        }
    }

    private void finishLogin(String authToken) {
        Log.d(TAG, "finishLogin() called with: authToken = [" + authToken + "]");
        if (authToken == null || authToken.length() == 0) {
            mLoading.setVisibility(View.GONE);
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        final Account account = new Account(mUsername, ACCOUNT_TYPE);
        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, mPassword, null);
            // Set contacts sync for this account.
            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
        } else {
            mAccountManager.setPassword(account, mPassword);
        }
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        mResultBundle = intent.getExtras();
        setResult(RESULT_OK, intent);
        mLoading.setVisibility(View.GONE);
        finish();
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_login;
    }

    @Override
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}
