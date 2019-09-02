package com.binlee.emoji.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class OsHelper {

    @SuppressLint("StaticFieldLeak")
    private volatile static Context sContext;
    private static int sRetryCount = 0;

    private OsHelper() {
        throw new AssertionError("no instance.");
    }

    public static boolean isMasterProcess() {
        final String processName = getProcessName(android.os.Process.myPid());
        return processName != null && !processName.contains(":");
    }

    public static String getProcessName(int pid) {
        ActivityManager am = (ActivityManager) app().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if (apps != null && apps.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo info : apps) {
                if (info.pid == pid) {
                    return info.processName;
                }
            }
        }
        return null;
    }

    public static Context app() {
        if (sContext == null) {
            // do your own initialization
            initialize();
        }
        return sContext;
    }

    private static void initialize() {
        if (sContext != null) {
            return;
        }
        try {
            final Class<?> clazz = Class.forName("com.binlee.emoji.DebugApplication");
            final Field sAppField = clazz.getDeclaredField("sApp");
            sAppField.setAccessible(true);
            final Object sApp = sAppField.get(null);
            if (sApp instanceof Context) {
                sContext = ((Application) sApp);
            }
            if (sContext != null) {
                return;
            }
            sContext = reflectHidenApi();
            // 如果失败则再尝试 4 次
            do {
                OsHelper.class.wait(120L);
                sContext = reflectHidenApi();
            } while (sContext == null && sRetryCount < 5);
        } catch (Throwable e) {
            sContext = null;
        } finally {
            OsHelper.class.notifyAll();
            sRetryCount = 0;
        }
    }

    private static Context reflectHidenApi() throws Throwable {
        sRetryCount++;
        final Class<?> clazz = Class.forName("android.app.ActivityThread");
        final Method method = clazz.getDeclaredMethod("currentApplication");
        method.setAccessible(true);
        return ((Application) method.invoke(null));
    }
}
