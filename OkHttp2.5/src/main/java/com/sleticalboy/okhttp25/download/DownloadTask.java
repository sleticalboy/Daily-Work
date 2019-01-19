package com.sleticalboy.okhttp25.download;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public abstract class DownloadTask implements Comparable<DownloadTask>, Cancelable {

    private final DownloadCallback mDownloadCallback;
    @CustomState
    private volatile String mState = CustomState.IDLE;
    private int mSequence;
    private DownloadTaskQueue mDownloadQueue;

    protected DownloadTask(DownloadCallback downloadCallback) {
        mDownloadCallback = downloadCallback;
    }

    public void pause() {
        mState = CustomState.PAUSED;
    }

    public void cancel() {
        mState = CustomState.CANCELED;
    }

    public void download() {
        if (Objects.equals(mState, CustomState.CANCELED)) {
            mDownloadCallback.onComplete();
            return;
        } else if (Objects.equals(mState, CustomState.PAUSED)) {
            mDownloadCallback.onPause();
        }
        mDownloadCallback.onStart(0L);
        if (Objects.equals(mState, CustomState.CANCELED)) {
            mDownloadCallback.onComplete();
        } else if (Objects.equals(mState, CustomState.PAUSED)) {
            mDownloadCallback.onPause();
        } else {
            mDownloadCallback.onStart(0L);
        }
    }

    @CustomState
    public String getState() {
        return mState;
    }

    @Override
    public boolean isCanceled() {
        return Objects.equals(mState, CustomState.CANCELED) || Objects.equals(mState, CustomState.PAUSED);
    }

    @Override
    public int compareTo(@NonNull DownloadTask another) {
        int left = this.getPriority();
        int right = another.getPriority();
        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - another.mSequence :
                right - left;
    }

    protected abstract int getPriority();

    protected void finish() {
        if (mDownloadQueue != null) {
            mDownloadQueue.finishTask(this);
        }
    }

    public void setDownloadQueue(DownloadTaskQueue queue) {
        mDownloadQueue = queue;
    }

    public void setSequenceNumber(int sequence) {
        mSequence = sequence;
    }
}
