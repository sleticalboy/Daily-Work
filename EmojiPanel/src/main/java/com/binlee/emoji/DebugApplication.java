package com.binlee.emoji;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class DebugApplication extends Application {

    @SuppressWarnings("Reflections")
    @SuppressLint("StaticFieldLeak")
    private static Application sApp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sApp = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
