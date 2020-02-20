package com.sleticalboy.dailywork.devices;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

/**
 * Created on 20-2-17.
 *
 * @author sleticalboy
 * @description
 */
public final class DeviceAdminUI extends BaseActivity {
    @Override
    protected int layoutResId() {
        return R.layout.activity_devices;
    }

    @Override
    protected void initView() {
        findViewById(R.id.activeDeviceAdmin).setOnClickListener(view -> {
            activeDeviceAdmin(this);
        });
        findViewById(R.id.stopDeviceAdmin).setOnClickListener(view -> {
            stopDeviceAdmin(this);
        });
    }

    private void stopDeviceAdmin(Context context) {
        // 会跳转到设备管理器列表
        // this will go to the list of admin apps
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.DeviceAdminSettings"));
        startActivity(intent);
    }

    private void activeDeviceAdmin(Context context) {
        // 会直接跳转到当前应用的 激活/关闭 界面
        // this will go directly to the activate/de-activate screen of the app you choose:
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(context,
                DevicesReceiver.class));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                context.getString(R.string.settings_device_admin_desc));
        startActivity(intent);
    }
}
