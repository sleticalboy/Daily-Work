package com.sleticalboy.dailywork.others;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.util.NotificationHelper;
import com.sleticalboy.util.ThreadHelper;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 20-4-2.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class NotificationsUI extends BaseActivity {

    private static final String TAG = "NotificationsUI";

    private NotificationManager mManager;
    private Handler mHandler;

    @Override
    protected void prepareWork(@Nullable final Bundle savedInstanceState) {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_notifications;
    }

    @Override
    protected void initView() {
        findViewById(R.id.notifyDelayWithLight).setOnClickListener(v -> {
            notifyDelayWithLight();
        });
        findViewById(R.id.notifyWithProgress).setOnClickListener(v -> {
            notifyWithProgress();
        });
    }

    private void notifyWithProgress() {
        final NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mgr == null) {
            return;
        }
        final Notification.Builder b = new Notification.Builder(this, TAG + "@" + hashCode());
        b.setContentTitle("test progress notification");
        b.setSubText("progress: 0%");
        b.setProgress(100, 0, false);
        b.setSmallIcon(android.R.drawable.stat_sys_upload);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            mgr.createNotificationChannel(new NotificationChannel(TAG, TAG, NotificationManager.IMPORTANCE_DEFAULT));
            b.setChannelId(TAG);
        }
        mgr.notify(NotificationsUI.this.hashCode(), b.build());

        final int[] progress = {0};
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int p = progress[0];
                if (p == 100) {
                    mgr.cancel(NotificationsUI.this.hashCode());
                    return;
                }
                b.setSubText("progress: " + p + "%");
                b.setProgress(100, p, false);
                mgr.notify(NotificationsUI.this.hashCode(), b.build());
                progress[0] = p + 10;
                mHandler.postDelayed(this, 1000L);
            }
        }, 100L);
    }

    private void notifyDelayWithLight() {
        final Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("测试呼吸灯闪烁");
        builder.setContentText("这是一个伴随着呼吸灯闪烁的通知.");
        builder.setLights(Color.RED, 3000, 3000);
        builder.setSmallIcon(R.drawable.ic_sms_light_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationHelper.SPECIAL_TAG);
        }
        builder.setDefaults(Notification.DEFAULT_ALL);
        final Notification not = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            not.color = Color.RED;
        }
        ThreadHelper.runOnMain(() -> {
            mManager.notify(NotificationHelper.SPECIAL_TAG, -1, not);
        }, 3000L);
    }
}
