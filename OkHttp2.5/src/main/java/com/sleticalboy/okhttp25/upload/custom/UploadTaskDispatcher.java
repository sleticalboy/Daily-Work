package com.sleticalboy.okhttp25.upload.custom;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

/**
 * Created on 16-6-1.
 * @author xiao
 */
public class UploadTaskDispatcher extends Thread {

    private final BlockingQueue<UploadTask> mQueue;
    private volatile boolean mQuit;

    public UploadTaskDispatcher(BlockingQueue<UploadTask> queue) {
        mQueue = queue;
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            UploadTask task;
            try {
                task = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }
            task.upload();
        }
    }
}
