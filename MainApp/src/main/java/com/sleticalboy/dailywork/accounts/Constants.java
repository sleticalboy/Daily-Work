package com.sleticalboy.dailywork.accounts;

import android.net.Uri;

public interface Constants {

    String ACCOUNT_PREFIX = "user";
    String ACCOUNT_PASSWORD = "test";
    String ACCOUNT_NAME = "sleticalboy@gmail.com";
    String ACCOUNT_TYPE = "com.sleticalboy.dailywork.accounts";
    String ACCOUNT_AUTH_TOKEN = ACCOUNT_TYPE + ".auth.token";
    Uri DB_URI = Uri.parse("content://" + ACCOUNT_TYPE);
}
