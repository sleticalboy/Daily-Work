package com.sleticalboy.learning.devices;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.sleticalboy.learning.R;

/**
 * Created on 20-2-17.
 *
 * @author sleticalboy
 */
public final class DevicesManager {

    private final Context mContext;
    private final DevicePolicyManager mMgr;
    private final ComponentName mComponent;

    public DevicesManager(Context context) {
        mContext = context;
        mMgr = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponent = new ComponentName(context.getPackageName(), DevicesReceiver.class.getName());
    }

    private boolean isActive() {
        return mMgr.isAdminActive(mComponent);
    }

    public void disableAdmin() {
        mMgr.removeActiveAdmin(mComponent);
    }

    public void startActivate() {
        if (isActive()) {
            Toast.makeText(mContext, R.string.activated, Toast.LENGTH_SHORT).show();
            return;
        }
        // 会直接跳转到当前应用的 激活/关闭 界面
        // this will go directly to the activate/de-activate screen of the app you choose:
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                mContext.getString(R.string.settings_device_admin_desc));
        mContext.startActivity(intent);
    }

    public void startDeactivate() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.not_activated, Toast.LENGTH_SHORT).show();
            return;
        }
        // 会跳转到设备管理器列表
        // this will go to the list of admin apps
        final Intent intent = new Intent();
        final String pkg = "com.android.settings";
        intent.setComponent(new ComponentName(pkg, pkg + ".DeviceAdminSettings"));
        mContext.startActivity(intent);
    }

    public void setWayOfLock() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        final Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
        mMgr.setPasswordQuality(mComponent, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
        mContext.startActivity(intent);
    }

    public void lockScreenNow() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        mMgr.lockNow();
    }

    public void lockScreenDelay(long delay) {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        mMgr.setMaximumTimeToLock(mComponent, delay);
    }

    public void resetDevice() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        // mMgr.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
    }

    public void forbidCamera() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        final boolean disabled = mMgr.getCameraDisabled(mComponent);
        Log.d("DevicesManager", "old state: " + disabled);
        final int res = disabled ? R.string.enable_camera : R.string.disable_camera;
        try {
            mMgr.setCameraDisabled(mComponent, !disabled);
            Toast.makeText(mContext, res, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.d("DevicesManager", "forbidCamera() error." + e);
        }
    }

    public void resetPassword() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        // 只能对未设置密码的设备重置密码, 若设备已有密码，则会抛出
        // java.lang.SecurityException: Admin cannot change current password
        try {
            boolean ret = mMgr.resetPassword("2580",
                    DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            Log.d("DevicesManager", "ret:" + ret);
        } catch (Exception e) {
            Log.w("DevicesManager", "reset password error", e);
        }
    }

    public void encryptStorage() {
        if (!isActive()) {
            Toast.makeText(mContext, R.string.active_device_admin, Toast.LENGTH_SHORT).show();
            return;
        }
        // final int result = mMgr.setStorageEncryption(mComponent, true);
        // Log.d("DevicesManager", "result:" + result);
    }
}
