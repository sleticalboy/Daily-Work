package com.binlee.sample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.binlee.sample.core.ArchManager;
import com.binlee.sample.core.DataSource;
import com.binlee.sample.core.IArchManager;
import com.binlee.sample.view.IView;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ArchCoreService extends Service {

    private final IArchManager mManager;
    private LocalBinder mBinder;

    public static void onBootCompleted(Context context) {
        DataSource.get().init(context, hasCache -> {
            // do stuff
        });

        final Intent service = new Intent(context, ArchCoreService.class)
                .putExtra("_bootstrap", true);
        context.startService(service);
    }

    public ArchCoreService() {
        mManager = new ArchManager();
    }

    @Override
    public void onCreate() {
        mManager.onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mManager.onStart();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new LocalBinder(mManager);
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mManager.onDestroy();
    }

    public static final class LocalBinder extends Binder {

        private final IArchManager mService;

        public LocalBinder(IArchManager service) {
            mService = service;
        }

        public void attachView(IView view) {
            mService.attachView(view);
        }

        public void detachView() {
            mService.detachView();
        }
    }
}
