package com.binlee.emoji.helper;

import android.os.SystemClock;
import android.util.Log;

import com.binlee.emoji.BuildConfig;

/**
 * Created on 19-1-18.
 *
 * @author leebin
 */
public final class LogHelper {

    private static final boolean LOGGABLE = BuildConfig.DEBUG;
    private static final int MAX_LOG_LENGTH = 4000;
    private static final double MILLIS_MULTIPLIER = 1d / Math.pow(10, 6);

    private LogHelper() {
        throw new AssertionError("no instance");
    }

    public static double elapsedMillis(long logTime) {
        return (logTime() - logTime) * MILLIS_MULTIPLIER;
    }

    public static long logTime() {
        return SystemClock.elapsedRealtimeNanos();
    }

    public static void debug(String tag, String msg) {
        debug(tag, msg, null);
    }

    /**
     * copy from OkHttp
     */
    public static void debug(String tag, String msg, Throwable thr) {
        if (!LOGGABLE) {
            return;
        }
        if (thr != null) {
            msg = msg + '\n' + Log.getStackTraceString(thr);
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = msg.length(); i < length; i++) {
            int newline = msg.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                Log.println(Log.DEBUG, tag, msg.substring(i, end));
                i = end;
            } while (i < newline);
        }
    }

    public static void dumpStackTrace(String tag) {
        final StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (final StackTraceElement element : traceElements) {
            Log.e(tag, "" + element);
        }
        Log.i(tag, "<============= the current stack trace ==============>");
    }
}
