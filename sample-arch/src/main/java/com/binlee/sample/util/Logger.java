package com.binlee.sample.util;

import android.util.Log;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public abstract class Logger {

    public static final Logger DEFAULT = new Logger() {
        @Override
        protected void log(String tag, String msg, int priority) {
            Log.println(priority, tag, msg);
        }
    };

    protected abstract void log(String tag, String msg, int priority);

    public void v(String tag, String msg) {
        log(tag, msg, Log.VERBOSE);
    }

    public void d(String tag, String msg) {
        log(tag, msg, Log.DEBUG);
    }

    public void i(String tag, String msg) {
        log(tag, msg, Log.INFO);
    }

    public void w(String tag, String msg) {
        log(tag, msg, Log.WARN);
    }

    public void e(String tag, String msg) {
        log(tag, msg, Log.ERROR);
    }
}
