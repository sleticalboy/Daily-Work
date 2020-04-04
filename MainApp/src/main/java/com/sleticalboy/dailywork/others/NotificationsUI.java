package com.sleticalboy.dailywork.others;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

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

    private NotificationManager mManager;

    @Override
    protected void prepareWork(@Nullable final Bundle savedInstanceState) {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_notifications;
    }

    @Override
    protected void initView() {
        final Button notifyDelayWithLight = findViewById(R.id.notifyDelayWithLight);
        notifyDelayWithLight.setOnClickListener(v -> notify(3000L, Color.RED));
    }

    private void notify(long delay, int color) {
        final Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("测试呼吸灯闪烁");
        builder.setContentText("这是一个伴随着呼吸灯闪烁的通知.");
        builder.setLights(color, 3000, 3000);
        builder.setSmallIcon(R.drawable.ic_sms_light_24dp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NotificationHelper.SPECIAL_TAG);
        }
        builder.setDefaults(Notification.DEFAULT_ALL);
        final Notification not = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            not.color = color;
        }
        ThreadHelper.runOnMain(() -> {
            mManager.notify(NotificationHelper.SPECIAL_TAG, -1, not);
        }, delay);
    }
}
