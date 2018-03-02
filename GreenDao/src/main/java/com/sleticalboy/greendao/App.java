package com.sleticalboy.greendao;

import android.app.Application;

import com.sleticalboy.greendao.bean.DaoMaster;
import com.sleticalboy.greendao.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created on 18-3-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class App extends Application {

    public static final boolean ENCRYPTED = false;

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(
                this, ENCRYPTED ? "student-db-encrypted" : "student-db");
        Database database = ENCRYPTED
                ? helper.getEncryptedReadableDb("super-secret")
                : helper.getWritableDb();
        mDaoSession = new DaoMaster(database).newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}
