package com.sleticalboy.okhttp25;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Created on 18-9-3.
 *
 * @author leebin
 */
public final class ContextProvider {

    private static Context sAppContext;

    static {
        try {
            @SuppressLint("PrivateApi") final Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method clazzMethod = clazz.getMethod("currentApplication");
            clazzMethod.setAccessible(true);
            sAppContext = (Context) clazzMethod.invoke(null, (Object[]) null);
            if (sAppContext == null) {
                @SuppressLint("PrivateApi") final Class<?> aClazz = Class.forName("android.app.AppGlobals");
                clazzMethod = aClazz.getMethod("getInitialApplication");
                sAppContext = (Context) clazzMethod.invoke(null, (Object[]) null);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static Handler getMainHandler() {
        return new Handler(sAppContext.getMainLooper());
    }

    @TargetApi(28)
    public static Executor getMainExecutor() {
        return sAppContext.getMainExecutor();
    }
}
