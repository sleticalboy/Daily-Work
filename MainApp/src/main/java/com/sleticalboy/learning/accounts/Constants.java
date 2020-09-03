package com.sleticalboy.learning.accounts;

import android.net.Uri;

public interface Constants {

    String ACCOUNT_PREFIX = "user";
    String ACCOUNT_PASSWORD = "test";
    String ACCOUNT_NAME = "sleticalboy@gmail.com";
    String ACCOUNT_TYPE = "com.sleticalboy.dailywork";
    String ACCOUNT_AUTH_TOKEN = "com.sleticalboy.dailywork.auth.token";
    Uri DB_URI = Uri.parse("content://com.sleticalboy.dailywork.contacts");
}
