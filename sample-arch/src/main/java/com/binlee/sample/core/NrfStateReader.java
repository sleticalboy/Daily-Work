package com.binlee.sample.core;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created on 21-2-25.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class NrfStateReader extends TimerTask implements IComponent {


    private static final String TAG = "StateReader";

    private final IArchManager mArch;
    private Timer mTimer;
    private Runnable mSlowLoopStarter;
    private boolean mCanceled = false;

    public NrfStateReader(IArchManager arch) {
        mArch = arch;
    }

    @Override
    public void onStart() {
        mTimer = new Timer(TAG);
        if (DataSource.get().hasCache()) startLoop(false);
    }

    @Override
    public void onDestroy() {
        cancelTask();
        mTimer.cancel();
    }

    @Override
    public void run() {
        if (!mCanceled) readOnce();
    }

    @Override
    public String toString() {
        return "ReadTask{mCanceled=" + mCanceled + '}';
    }

    public void startIfNeeded() {
        startLoop(true);
    }

    private void startLoop(boolean fast) {
        cancelTask();
        mTimer.schedule(this, fast ? 500L : 1000L, fast ? 500L : 1000L);
        if (!fast) return;
        if (mSlowLoopStarter == null) mSlowLoopStarter = () -> {
            startLoop(false);
        };
        mArch.handler().postDelayed(mSlowLoopStarter, 10000L);
    }

    private void cancelTask() {
        mCanceled = super.cancel();
    }

    private void readOnce() {
        checkStateLost();
    }

    private void checkStateLost() {
    }
}
