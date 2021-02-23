package com.binlee.sample.view;

import android.os.Handler;
import android.os.Looper;

import com.binlee.sample.core.DataSource;

/**
 * Created on 21-2-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class ViewProxy implements IView {

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private IView mTarget;

    public void setTarget(IView target) {
        mTarget = target;
    }

    @Override
    public void onClearInfo(DataSource.Device device, boolean remote) {
        if (mTarget != null) {
            MAIN_HANDLER.post(() -> mTarget.onClearInfo(device, remote));
        }
    }
}
