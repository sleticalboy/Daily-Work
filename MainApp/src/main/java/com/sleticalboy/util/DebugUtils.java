package com.sleticalboy.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 20-3-27.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DebugUtils {

    private static final String TAG = "DebugUtils";
    /* copy from IBinder */
    private static final int SYSPROPS_TRANSACTION = ('_' << 24) | ('S' << 16) | ('P' << 8) | 'R';
    private static final String SERVICE_MANAGER = "android.os.ServiceManager";
    private static final String SYSTEM_PROP = "android.os.SystemProperties";
    private static Class sServiceMgr, sSystemProp;
    private static Method sListServices, sCheckService, sSet;
    private static boolean sIsWorking = false;

    public static void openSettings(Context context) {
        final Intent intent = new Intent("android.settings.SETTINGS");
        final String pkg = "com.android.settings";
        intent.setComponent(new ComponentName(pkg, pkg + ".Settings"));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void debugLayout(boolean open) {
        // show layout bound
        // final boolean isEnabled = SystemProperties.getBoolean("debug.layout", false/*default*/);
        if (sIsWorking) {
            return;
        }
        sIsWorking = true;
        try {
            ensure();
            sSet.invoke(null, "debug.layout", open ? "true" : "false");
        } catch (Throwable e) {
            Log.e(TAG, "debugLayout set properties error", e);
        }
        new PokerTask().execute();
    }

    private static void ensure() throws Throwable {
        // android.os.SystemProperties
        if (sServiceMgr == null) {
            sServiceMgr = Class.forName(SERVICE_MANAGER);
        }
        if (sListServices == null) {
            sListServices = sServiceMgr.getDeclaredMethod("listServices");
        }
        if (sCheckService == null) {
            sCheckService = sServiceMgr.getDeclaredMethod("checkService", String.class);
        }
        // android.os.SystemProperties
        if (sSystemProp == null) {
            sSystemProp = Class.forName(SYSTEM_PROP);
        }
        if (sSet == null) {
            sSet = sSystemProp.getDeclaredMethod("set", String.class, String.class);
        }
    }

    public static class PokerTask extends AsyncTask<Void, Void, Void> {

        String[] listServices() throws Throwable {
            return (String[]) sListServices.invoke(null);
        }

        IBinder checkService(String service) throws Throwable {
            return (IBinder) sCheckService.invoke(null, service);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String[] services = new String[0];
            try {
                services = listServices();
            } catch (Throwable e) {
                Log.e(TAG, "List all services error", e);
            }
            final List<String> failedServices = new ArrayList<>();
            for (String service : services) {
                final Parcel data = Parcel.obtain();
                try {
                    checkService(service).transact(/*IBinder.*/SYSPROPS_TRANSACTION, data,
                            null, 0);
                } catch (Throwable e) {
                    // Log.i(TAG, "Someone wrote a bad service '" + service
                    //         + "' that doesn't like to be poked", e);
                    failedServices.add(service);
                } finally {
                    data.recycle();
                }
            }
            Log.w(TAG, "All failed Services:" + failedServices);
            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            sIsWorking = false;
        }
    }

}
