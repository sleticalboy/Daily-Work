package com.sleticalboy.okhttp25.upload.custom;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Created on 11/15/16.
 *
 * @author xiao
 */
public abstract class UploadTask implements Comparable<UploadTask>, Cancelable {

    @UploadState
    private volatile String mState = UploadState.IDLE;
    private int mSequence;
    private UploadTaskQueue mUploadQueue;

    public UploadTask() {
    }

    public void pause() {
        mState = UploadState.PAUSED;
    }

    public void upload() {
        if (Objects.equals(mState, UploadState.CANCELED)) {
            onFinish();
            return;
        } else if (Objects.equals(mState, UploadState.PAUSED)) {
            onPause();
        }

        onStartUpload();
        if (Objects.equals(getState(), UploadState.CANCELED)) {
            onFinish();
        } else if (Objects.equals(getState(), UploadState.PAUSED)) {
            onPause();
        } else {
            onStartRegister();
        }
    }

    @UploadState
    public String getState() {
        return mState;
    }

    protected abstract void onStartRegister();

    protected abstract void onStartUpload();

    protected abstract void onPause();

    protected abstract void onFinish();

    public abstract int getTaskId();

    public void cancel() {
        mState = UploadState.CANCELED;
    }

    @Override
    public int compareTo(@NonNull UploadTask another) {
        int left = this.getPriority();
        int right = another.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - another.mSequence :
                right - left;
    }

    public abstract int getPriority();

    public void setSequence(int sequence) {
        this.mSequence = sequence;
    }

    public UploadTask setUploadQueue(UploadTaskQueue uploadTaskQueue) {
        mUploadQueue = uploadTaskQueue;
        return this;
    }

    protected void finish() {
        if (mUploadQueue != null) {
            mUploadQueue.finish(this);
        }
    }

    public void publicProgress(int taskId, long uploadedSize, long totalSize) {
        onProgressUpdate(taskId, uploadedSize, totalSize);
    }

    protected void onProgressUpdate(int taskId, long uploadedSize, long totalSize) {

    }

    @Override
    public boolean isCanceled() {
        return Objects.equals(getState(), UploadState.CANCELED) || Objects.equals(getState(), UploadState.PAUSED);
    }

}
