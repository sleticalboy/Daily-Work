package com.sleticalboy.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created on 20-4-2.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ThreadHelper {

    private static Handler sMain;
    private static Handler sWorker;

    private ThreadHelper() {
        //no instance
    }

    private static void ensureMain() {
        if (sMain != null) {
            return;
        }
        sMain = new Handler(Looper.getMainLooper());
    }

    private static void ensureWorker() {
        if (sWorker != null) {
            return;
        }
        final HandlerThread thread = new HandlerThread("ThreadHelper");
        thread.start();
        sWorker = new Handler(thread.getLooper());
    }

    private static void exec(boolean main, Runnable task, long delay) {
        if (task == null) {
            return;
        }
        if (main) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                task.run();
                return;
            }
            ensureMain();
        } else {
            ensureWorker();
        }
        (main ? sMain : sWorker).postDelayed(task, delay);
    }

    public static void runOnMain(Runnable task) {
        exec(true, task, -1);
    }

    public static void runOnMain(Runnable task, long delay) {
        exec(true, task, delay);
    }

    public static void runOnWorker(Runnable task) {
        exec(false, task, -1);
    }

    public static void runOnWorker(Runnable task, long delay) {
        exec(false, task, delay);
    }

}
