package com.sleticalboy.dailywork.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.util.DevicesUtils;
import com.sleticalboy.dailywork.util.TimeUtils;

/**
 * Created on 18-5-11.
 *
 * @author sleticalboy
 * @description
 */
public class AlarmActivity extends BaseActivity {

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAction.equals(intent.getAction())) {
                Log.d("AlarmActivity", intent.getAction());
            }
        }
    };
    private String mAction;
    private AlarmManager mAlarmManager;

    @Override
    protected void prepareWork() {
        mAction = getPackageName() + ".TIMER_ATTEND";
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_alarm;
    }

    @Override
    protected void initView() {
        TextView tvTime = findViewById(R.id.tvTime);
        final long start = TimeUtils.str2millis("2018-05-11T00:00:00+08:00");
        final long end = TimeUtils.str2millis("2018-05-11T09:03:00+08:00");
        Log.d("AlarmActivity", "start:" + start);
        Log.d("AlarmActivity", "end:" + end);
        String time = "end - start = " + (end - start);
        Log.d("AlarmActivity", time);
        tvTime.setText(time);
        final String msg = TimeUtils.millis2str(System.currentTimeMillis());
        Log.d("AlarmActivity", msg);
        tvTime.append("\n" + msg);
        final double distance = TimeUtils.getDistance(34.7704267, 113.7584882, 34.7703974, 113.7583287);
        tvTime.append("\ndistance = " + distance);
        tvTime.append("\nmac address = " + DevicesUtils.getMacAddress(this));
        tvTime.append("\nwifi mac address = " + DevicesUtils.getConnectedWifiMacAddress(this));
    }

    @Override
    protected void initData() {
        IntentFilter filter = new IntentFilter(mAction);
        registerReceiver(alarmReceiver, filter);

        Intent intent = new Intent(mAction);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
        assert mAlarmManager != null;
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, operation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(alarmReceiver);
    }
}
