package com.binlee.sample.core;

import com.binlee.sample.event.AsyncEvent;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class EventExecutor extends Thread implements IComponent {

    private static final String TAG = "Dispatcher";

    private static final int CAPACITY = 2;

    private final ArrayBlockingQueue<AsyncEvent> mTasks = new ArrayBlockingQueue<>(CAPACITY);
    private boolean mReleased = false;
    private AsyncEvent mEvent;

    public EventExecutor() {
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
            AsyncEvent call = null;
            try {
                call = mTasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (call != null) {
                mEvent = call;
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

    public boolean submit(AsyncEvent event) {
        if (event == null || mEvent.equals(event) || mTasks.contains(event)) {
            return false;
        }
        try {
            mTasks.put(event);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void finish(AsyncEvent event, int reason) {
        if (event == null || mEvent == null) {
            return;
        }
        if (mEvent.equals(event) && !event.isFinished()) {
            synchronized (mTasks) {
                event.onFinish(reason);
                mEvent = null;
                mTasks.notifyAll();
            }
        }
    }

    public void abortAll() {
        for (final AsyncEvent event : mTasks) {
            finish(event, AsyncEvent.REASON_ABORTED);
        }
    }
}
