package com.sleticalboy.dailywork;

import android.app.Application;
import android.content.Context;

import com.sleticalboy.dailywork.bean.DaoMaster;
import com.sleticalboy.dailywork.bean.DaoSession;
import com.sleticalboy.dailywork.cache.ACache;

import org.greenrobot.greendao.database.Database;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created on 18-3-5.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class MainApp extends Application {

    private static DaoSession sDaoSession;
    private static Reference<Context> mRefContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mRefContext = new WeakReference<>(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initCache();
        initDB();
    }

    private void initCache() {
        ACache.mCacheName = "DailyWork";
    }

    private void initDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "daily-work");
        Database database = helper.getWritableDb();
        sDaoSession = new DaoMaster(database).newSession();
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }

    public static Context getContext() {
        return mRefContext.get();
    }
}
