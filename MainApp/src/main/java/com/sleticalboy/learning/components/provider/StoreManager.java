package com.sleticalboy.learning.components.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public final class StoreManager {

    private final DbHelper mHelper;

    private static class SingletonHolder {
        final static StoreManager MANAGER = new StoreManager();
    }

    private StoreManager() {
        this(null);
    }

    private StoreManager(final Context context) {
        mHelper = context == null ? null : new DbHelper(context);
    }

    public static void init(final Context context) {
        new StoreManager(context);
    }

    public static StoreManager get() {
        return SingletonHolder.MANAGER;
    }

    private DbHelper helper() {
        return mHelper;
    }

    public SQLiteDatabase readableDb() {
        return get().helper().getReadableDatabase();
    }

    public SQLiteDatabase writableDb() {
        return get().helper().getWritableDatabase();
    }

    static private class DbHelper extends SQLiteOpenHelper {

        // public static final int STATUS_CONNECTED = 0;
        // public static final int STATUS_NOT_CONNECTED = 1;
        // public static final int STATUS_DISCONNECTING = 2;
        // public static final int STATUS_CONNECTING = 3;
        // public static final int STATUS_WRITE_CONFIG = 4;
        // public static final int STATUS_SWITCH_MODE = 5;
        // public static final int STATUS_SWITCH_DONE = 6;

        // 用户主动断开时：
        // 1、需要同步断开 profile；2、重连时需要同步连上 profile
        // public static final int DISCONNECT_BY_USER = 1;
        // 同 DISCONNECT_BY_USER
        // public static final int DISCONNECT_BY_AUDIO_ROUTE = 2;
        // 同 DISCONNECT_BY_USER
        // public static final int DISCONNECT_BY_DEVICE_SLEEP = 3;
        // 非用户主动断开：
        // 实现反连
        // public static final int DISCONNECT_BY_LOST_HOST = 4;

        public DbHelper(@Nullable Context context) {
            super(context, "connected_devices.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE device_list(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(15), " +
                    "mac VARCHAR(15), " +
                    "pipe INTEGER, " +
                    "channels VARCHAR(15), " +
                    "disconnect_type INTEGER DEFAULT 1, " +
                    "status INTEGER DEFAULT 1" +
                    ")"
            );
            migrantFromCache(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //
        }

        private void migrantFromCache(SQLiteDatabase db) {
        }
    }
}
