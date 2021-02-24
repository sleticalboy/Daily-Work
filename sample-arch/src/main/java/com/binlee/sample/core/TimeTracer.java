package com.binlee.sample.core;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * Created on 21-2-24.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class TimeTracer implements Handler.Callback {

    private final Handler mHandler;

    public TimeTracer(Handler handler) {
        if (handler instanceof InjectableHandler) {
            ((InjectableHandler) handler).injectCallback(this);
        }
        mHandler = handler;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }
}
