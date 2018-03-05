package com.sleticalboy.greendao;

import android.app.Application;
import android.content.Context;

import com.sleticalboy.greendao.bean.DaoMaster;
import com.sleticalboy.greendao.bean.DaoSession;

import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Created on 18-3-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class App extends Application {

    public static final boolean ENCRYPTED = false;

    private static AbstractDaoSession mDaoSession;
    private static Reference<Context> sBaseContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sBaseContext = new WeakReference<>(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 1,获取 helper
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(
                this, ENCRYPTED ? "student-db-encrypted" : "student-db");
        // 2,获取数据库
        Database database = ENCRYPTED
                ? helper.getEncryptedReadableDb("super-secret")
                : helper.getWritableDb();
        // 3,实例化 DaoSession
        mDaoSession = new DaoMaster(database).newSession();
    }

    public static AbstractDaoSession getDaoSession() {
        return mDaoSession;
    }

    public static Context getContext() {
        return sBaseContext.get();
    }
}
