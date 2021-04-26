package com.sleticalboy.http.download;

import androidx.annotation.WorkerThread;

import com.sleticalboy.http.FileTypeUtils;
import com.sleticalboy.http.HttpClient;
import com.sleticalboy.http.callback.ProgressCallback;
import com.sleticalboy.http.interceptor.ProgressInterceptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created on 18-9-3.
 *
 * @author leebin
 */
public final class OkDownloader {

    private static final int STATE_RESUME = 0x100;
    private static final int STATE_PAUSE = 0x101;
    private static final int STATE_CANCEL = 0x102;

    private final HttpClient mHttpClient;
    private final String mUrl;
    private final File mSaveFile;
    private DownloadCallback mCallback;
    private boolean mIsPause = false;
    private boolean mIsCancel = false;
    private boolean mIsDownloading = false;
    /*** 断点的位置 ***/
    private long breakPoint = 0L;

    public OkDownloader(String downloadUrl, String savePath) {
        this(downloadUrl, new File(savePath), false);
    }

    public OkDownloader(String downloadUrl, File saveFile, boolean isNeedProgress) {
        mUrl = downloadUrl;
        mSaveFile = saveFile;
        mHttpClient = HttpClient.getInstance();
        if (isNeedProgress) {
            mHttpClient.addInterceptor(ProgressInterceptor.newInstance());
            ProgressInterceptor.addCallback(mUrl, new ProgressCallback.SimpleCallback() {

                @Override
                public void onPreExecute(long total) {
                    if (mCallback != null) {
                        // 断点续传时此处为剩余文件的长度
                        // total + breakPoint 是文件的总长度
                        mHttpClient.getMainHandler().post(() -> mCallback.onStart(total));
                    }
                }

                @Override
                public void onProgress(float progress, long bytesTotalRead) {
                    if (mCallback != null) {
                        mHttpClient.getMainHandler().post(() -> mCallback.onProgress(progress));
                    }
                }
            });
        }
    }

    /**
     * 开始下载, after {@link OkDownloader#setDownloadCallback(DownloadCallback)}
     */
    public void start() {
        if (mIsPause) {
            resume();
            return;
        }
        mIsCancel = false;
        mIsPause = false;
        download(0L);
    }

    /**
     * 开始下载
     *
     * @param startPoint 位置
     */
    private void download(final long startPoint) {
        //
    }

    @WorkerThread
    private void saveFile(/*ResponseBody body,*/ long startPoint) {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            // inputStream = body.byteStream();
            randomAccessFile = new RandomAccessFile(mSaveFile, "rw");
            randomAccessFile.seek(startPoint);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                if (mIsCancel) {
                    notifyStateChange(STATE_CANCEL);
                    return;
                } else if (mIsPause) {
                    notifyStateChange(STATE_PAUSE);
                    return;
                } else {
                    breakPoint = (startPoint += len);
                    randomAccessFile.write(buffer, 0, len);
                }
            }
            if (mCallback != null) {
                ProgressInterceptor.removeCallback(mUrl);
                resetDownloader();
                mHttpClient.getMainHandler().post(() -> mCallback.onComplete());
            }
        } catch (IOException ignored) {
            if (mCallback != null) {
                mHttpClient.getMainHandler().post(() -> mCallback.onError(ignored));
            }
        } finally {
            FileTypeUtils.closeSilently(inputStream);
            FileTypeUtils.closeSilently(randomAccessFile);
        }
    }

    private void resetDownloader() {
        mIsCancel = false;
        mIsDownloading = false;
        mIsPause = false;
    }

    private void notifyStateChange(int state) {
        if (mCallback != null) {
            if (state == STATE_CANCEL) {
                mHttpClient.getMainHandler().post(() -> mCallback.onCancel());
            } else if (state == STATE_PAUSE) {
                mHttpClient.getMainHandler().post(() -> mCallback.onPause());
            } else if (state == STATE_RESUME) {
                mHttpClient.getMainHandler().post(() -> mCallback.onResume());
            }
        }
    }

    /**
     * 暂停下载
     */
    public void pause() {
        if (mIsPause) {
            return;
        }
        mIsPause = true;
        mIsCancel = false;
        mIsDownloading = false;
    }

    /**
     * 恢复下载
     */
    public void resume() {
        if (mIsDownloading) {
            return;
        }
        mIsDownloading = true;
        mIsPause = false;
        mIsCancel = false;
        notifyStateChange(STATE_RESUME);
        download(breakPoint);
    }

    /**
     * 设置回调, before {@link OkDownloader#start()}
     *
     * @param callback {@link DownloadCallback}
     */
    public void setDownloadCallback(DownloadCallback callback) {
        mCallback = callback;
    }

    /**
     * 取消下载
     */
    public void cancel() {
        if (mIsCancel) {
            return;
        }
        mIsCancel = true;
        mIsDownloading = false;
        mIsPause = false;
    }
}
