package com.binlee.sample.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 21-2-24.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class LifecycleDetector implements IComponent, Application.ActivityLifecycleCallbacks {

    private final Application mApp;

    public LifecycleDetector(Context context) {
        context = context.getApplicationContext();
        if (!(context instanceof Application)) {
            throw new RuntimeException("Application is not initialized.");
        }
        mApp = (Application) context;
    }

    @Override
    public void onStart() {
        mApp.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onDestroy() {
        if (mApp != null) mApp.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
