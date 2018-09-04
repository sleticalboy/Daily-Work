package com.sleticalboy.okhttp25.download;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.sleticalboy.okhttp25.CloseUtils;
import com.sleticalboy.okhttp25.http.HttpClient;
import com.sleticalboy.okhttp25.http.builder.AbstractBuilder;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.sleticalboy.okhttp25.upload.custom.ProgressCallback;
import com.sleticalboy.okhttp25.upload.custom.ProgressInterceptor;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class ProgressDownloader {

    public static final int STATE_RESUME = 0x100;
    public static final int STATE_PAUSE = 0x101;
    public static final int STATE_CANCEL = 0x102;

    private final HttpClient mHttpClient;
    private final String mUrl;
    private final File mSaveFile;
    private DownloadCallback mCallback;
    private Call mCall;
    private boolean mIsPause = false;
    private boolean mIsCancel = false;

    public ProgressDownloader(String downloadUrl, String savePath) {
        this(downloadUrl, new File(savePath));
    }

    public ProgressDownloader(String downloadUrl, File saveFile) {
        mUrl = downloadUrl;
        mSaveFile = saveFile;
        mHttpClient = HttpClient.getInstance().interceptor(ProgressInterceptor.newInstance());
        ProgressInterceptor.addCallback(mUrl, new ProgressCallback.SimpleCallback() {
            @Override
            public void onPreExecute(long contentLength) {
                if (mCallback != null) {
                    mHttpClient.getMainHandler().post(() -> mCallback.onStart(contentLength));
                }
            }

            @Override
            public void onProgress(int progress, long bytesTotalRead) {
                if (mCallback != null) {
                    mHttpClient.getMainHandler().post(() -> mCallback.onProgress(progress, bytesTotalRead));
                }
            }
        });
    }

    /**
     * 开始下载, after {@link ProgressDownloader#setDownloadCallback(DownloadCallback)}
     */
    public void start() {
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
        String threadName = Thread.currentThread().getName();
        Log.d("ProgressDownloader", "current thread is " + threadName);
        InputStream inputStream = null;
//        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            inputStream = body.byteStream();
            randomAccessFile = new RandomAccessFile(mSaveFile, "rw");
            randomAccessFile.seek(startPoint);
//            channel = randomAccessFile.getChannel();
//            final MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, startPoint, body.contentLength());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                if (mIsCancel) {
//                    notifyStateChange(STATE_CANCEL);
                    return;
                } else if (mIsPause) {
//                    notifyStateChange(STATE_PAUSE);
                    return;
                } else {
                    randomAccessFile.write(buffer, 0, len);
                }
            }
            if (mCallback != null) {
                mHttpClient.getMainHandler().post(() -> {
                    mCallback.onComplete();
                    ProgressInterceptor.removeCallback(mUrl);
                });
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

    private void notifyStateChange(int state) {
        if (mCallback != null) {
            if (state == STATE_CANCEL) {
                mCallback.onCancel();
            } else if (state == STATE_PAUSE) {
                mCallback.onPause();
            } else if (state == STATE_RESUME) {
                mCallback.onResume();
            }
        }
    }

    /**
     * 暂停下载
     */
    public void pause() {
        mIsPause = true;
        notifyStateChange(STATE_PAUSE);
//        if (mCall != null && !mCall.isCanceled()) {
//            mCall.cancel();
//        }
    }

    /**
     * 恢复下载
     *
     * @param startPoint 从哪里开始
     */
    public void resume(long startPoint) {
        mIsPause = false;
        mIsCancel = false;
        notifyStateChange(STATE_RESUME);
        download(startPoint);
    }

    /**
     * 设置回调, before {@link ProgressDownloader#start()}
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
        mIsCancel = true;
        notifyStateChange(STATE_CANCEL);
    }
}
