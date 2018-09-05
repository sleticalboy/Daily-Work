package com.sleticalboy.okhttp25.download;

import android.support.annotation.WorkerThread;

import com.sleticalboy.okhttp25.CloseUtils;
import com.sleticalboy.okhttp25.http.HttpClient;
import com.sleticalboy.okhttp25.http.builder.AbstractBuilder;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.sleticalboy.okhttp25.upload.custom.ProgressCallback;
import com.sleticalboy.okhttp25.upload.custom.ProgressInterceptor;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class OkDownloader {

    private static final int STATE_RESUME = 0x100;
    private static final int STATE_PAUSE = 0x101;
    private static final int STATE_CANCEL = 0x102;

    private final HttpClient mHttpClient;
    private final String mUrl;
    private final File mSaveFile;
    private DownloadCallback mCallback;
    private Call mCall;
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
            mHttpClient.interceptor(ProgressInterceptor.newInstance());
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
        final List<Interceptor> interceptors = mHttpClient.interceptors();
        if (interceptors.size() != 0) {
            for (final Interceptor interceptor : interceptors) {
                if (interceptor != null && interceptor instanceof ProgressInterceptor) {
                    ((ProgressInterceptor) interceptor).setBreakPoint(startPoint);
                }
            }
        }
        AbstractBuilder builder = new GetBuilder().url(mUrl).breakPoint(startPoint);
        mCall = mHttpClient.getOkHttpClient().newCall(builder.build());
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (mCallback != null) {
                    mHttpClient.getMainHandler().post(() -> mCallback.onError(e));
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                saveFile(response.body(), startPoint);
            }
        });
    }

    @WorkerThread
    private void saveFile(ResponseBody body, long startPoint) {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            inputStream = body.byteStream();
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
            CloseUtils.closeSilently(inputStream);
            CloseUtils.closeSilently(randomAccessFile);
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
