package com.sleticalboy.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcel;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created on 20-3-27.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DebugUtils {

    private static final String TAG = "DebugUtils";
    /**
     * @hide
     */
    private static final int SYSPROPS_TRANSACTION = ('_' << 24) | ('S' << 16) | ('P' << 8) | 'R';
    private static final String SERVICE_MANAGER = "android.os.ServiceManager";
    private static final String SYSTEM_PROP = "android.os.SystemProperties";
    private static Class sServiceMgr, sSystemProp;
    private static Method sListService, sCheckService, sSet;
    private static boolean sIsWorking = false;

    public static void openSettings(Context context) {
        final Intent intent = new Intent("android.settings.SETTINGS");
        final String pkg = "com.android.settings";
        intent.setComponent(new ComponentName(pkg, pkg + ".Settings"));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void debugLayout(boolean open) {
        // show layout bound
        // final boolean isEnabled = SystemProperties.getBoolean("debug.layout", false/*default*/);
        try {
            ensure();
            sSet.invoke(null, "debug.layout", open ? "true" : "false");
        } catch (Throwable e) {
            Log.e(TAG, "debugLayout set properties error", e);
        }
        if (sIsWorking) {
            return;
        }
        new PokerTask().execute();
    }

    private static void ensure() throws Throwable {
        // android.os.SystemProperties
        if (sServiceMgr == null) {
            sServiceMgr = Class.forName(SERVICE_MANAGER);
        }
        if (sListService == null) {
            sListService = sServiceMgr.getDeclaredMethod("listService");
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
            return (String[]) sListService.invoke(null);
        }

        IBinder checkService(String service) throws Throwable {
            return (IBinder) sCheckService.invoke(null, service);
        }

        @Override
        protected void onPreExecute() {
            sIsWorking = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String[] services = new String[0];
            try {
                services = listServices();
            } catch (Throwable e) {
                Log.e(TAG, "List all services error", e);
            }
            for (String service : services) {
                final Parcel data = Parcel.obtain();
                try {
                    checkService(service).transact(/*IBinder.*/SYSPROPS_TRANSACTION, data,
                            null, 0);
                } catch (Throwable e) {
                    Log.i(TAG, "Someone wrote a bad service '" + service
                            + "' that doesn't like to be poked", e);
                } finally {
                    data.recycle();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            sIsWorking = false;
        }
    }

}
