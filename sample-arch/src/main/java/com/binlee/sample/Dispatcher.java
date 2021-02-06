package com.binlee.sample;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class Dispatcher extends Thread implements IComponent {

    private static final int CAPACITY = 2;

    private final ArrayBlockingQueue<AsyncCall> mTasks = new ArrayBlockingQueue<>(CAPACITY);
    private boolean mReleased = false;
    private AsyncCall mCall;

    public Dispatcher() {
    }

    @Override
    public void onStart() {
        mReleased = false;
        super.start();
    }

    @Override
    public void onDestroy() {
        interrupt();
        mReleased = true;
    }

    @Override
    public void run() {
        while (!isInterrupted() && !mReleased) {
            AsyncCall call = null;
            try {
                call = mTasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (call != null) {
                mCall = call;
                try {
                    call.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                synchronized (mTasks) {
                    try {
                        mTasks.wait(40000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean enqueueCall(AsyncCall call) {
        if (call == null || mCall.equals(call) || mTasks.contains(call)) {
            return false;
        }
        try {
            mTasks.put(call);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void finish(AsyncCall call) {
        if (call == null || mCall == null) {
            return;
        }
        if (mCall.equals(call)) {
            synchronized (mTasks) {
                call.onFinish();
                mTasks.notifyAll();
            }
        }
    }

    public void abortAll() {
        for (final AsyncCall task : mTasks) {
            finish(task);
        }
    }
}
