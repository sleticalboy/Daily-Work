package com.sleticalboy.okhttp25;

import android.annotation.SuppressLint;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class ContextProvider {

    private static Context sApplicationContext;

    static {
        try {
            @SuppressLint("PrivateApi") final Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method clazzMethod = clazz.getMethod("currentApplication");
            clazzMethod.setAccessible(true);
            sApplicationContext = (Context) clazzMethod.invoke(null, (Object[]) null);
            if (sApplicationContext == null) {
                @SuppressLint("PrivateApi") final Class<?> aClazz = Class.forName("android.app.AppGlobals");
                clazzMethod = aClazz.getMethod("getInitialApplication");
                sApplicationContext = (Context) clazzMethod.invoke(null, (Object[]) null);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }

    public static Context getApplicationContext() {
        return sApplicationContext;
    }
}
