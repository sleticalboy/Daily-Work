package com.sleticalboy.dailywork.devices;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * Created on 20-2-17.
 *
 * @author sleticalboy
 * @description
 */
public final class DevicesManager {

    private final Context mContext;
    private final DevicePolicyManager mMgr;
    private final ComponentName mComponent;

    public DevicesManager(Context context) {
        mContext = context;
        mMgr = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final String pkg = context.getPackageName();
        mComponent = new ComponentName(pkg, pkg + ".DevicesReceiver");
    }

    public ComponentName getComponent() {
        return mComponent;
    }

    public boolean isAdminActivive() {
        return mMgr.isAdminActive(mComponent);
    }

    public void disableAdmin() {
        mMgr.removeActiveAdmin(mComponent);
    }
}
