package com.binlee.emoji.helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;

/**
 * Created on 19-7-24.
 *
 * @author leebin
 */
public final class ThreadHelper {
    
    private static Handler sAsyncHandler;
    private static Handler sMainHandler;
    
    private ThreadHelper() {
        throw new AssertionError("no instance");
    }
    
    public static Handler mainHandler() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
        return sMainHandler;
    }
    
    public synchronized static Handler asyncHandler() {
        if (sAsyncHandler == null) {
            final HandlerThread handlerThread = new HandlerThread(
                    "sAsyncHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();
            // getLooper() 方法可能会发生阻塞
            sAsyncHandler = new Handler(handlerThread.getLooper());
        }
        return sAsyncHandler;
    }
    
    public static void runOnMain(final Runnable task) {
        mainHandler().post(task);
    }
    
    public static void runOnMain(final Runnable task, final long delayMillis) {
        mainHandler().postDelayed(task, delayMillis);
    }
    
    public static void runOnWorker(final Runnable task) {
        asyncHandler().post(task);
    }
    
    public static void runOnWorker(final Runnable task, final long delayMillis) {
        asyncHandler().postDelayed(task, delayMillis);
    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
