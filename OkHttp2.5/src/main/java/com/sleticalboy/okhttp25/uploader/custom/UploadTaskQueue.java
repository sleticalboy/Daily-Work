package com.sleticalboy.okhttp25.uploader.custom;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 16-6-1.
 *
 * @author xiao
 */
public class UploadTaskQueue {

    public static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 1;
    private final Set<UploadTask> mCurrentQueue = new HashSet<>();
    private final PriorityBlockingQueue<UploadTask> mUploadQueue = new PriorityBlockingQueue<>();
    private UploadTaskDispatcher[] mDispatchers;
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    public UploadTaskQueue() {
        this(DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public UploadTaskQueue(int threadPoolSize) {
        mDispatchers = new UploadTaskDispatcher[threadPoolSize];
    }

    public void start() {
        stop();
        for (int i = 0; i < mDispatchers.length; i++) {
            UploadTaskDispatcher dispatcher = new UploadTaskDispatcher(mUploadQueue);
            mDispatchers[i] = dispatcher;
            dispatcher.start();
        }
    }

    public void stop() {
        for (UploadTaskDispatcher dispatcher : mDispatchers) {
            if (dispatcher != null) {
                dispatcher.quit();
            }
        }
    }

    public void add(UploadTask task) {
        task.setUploadQueue(this);
        synchronized (mCurrentQueue) {
            mCurrentQueue.add(task);
        }
        task.setSequence(getSequenceNumber());
        mUploadQueue.add(task);
    }

    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    void finish(UploadTask task) {
        synchronized (mCurrentQueue) {
            task.cancel();
            mCurrentQueue.remove(task);
        }
    }

    public void stopAllTask() {
        synchronized (mCurrentQueue) {
            for (UploadTask task : mCurrentQueue) {
                task.pause();
            }
        }
    }

    public void cancelAll(TaskFilter filter) {
        synchronized (mCurrentQueue) {
            for (UploadTask task : mCurrentQueue) {
                if (filter.apply(task)) {
                    task.cancel();
                }
            }
        }
    }

    public int getCurrentTaskCount() {
        return mCurrentQueue.size();
    }

    public Set<UploadTask> getCurrentTasks() {
        return mCurrentQueue;
    }

    public interface TaskFilter {
        boolean apply(UploadTask task);
    }
}
