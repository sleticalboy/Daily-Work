package com.sleticalboy.okhttp25.body;

import androidx.annotation.NonNull;

import com.sleticalboy.okhttp25.callback.ProgressCallback;
import com.sleticalboy.okhttp25.interceptor.ProgressInterceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created on 18-9-3.
 *
 * @author leebin
 */
public final class ProgressResponseBody extends ResponseBody {

    private final ResponseBody mRawBody;
    private final long mBreakPoint;
    private ProgressCallback mCallback;
    private BufferedSource mSource;

    public ProgressResponseBody(String url, ResponseBody raw, long breakPoint) throws IOException {
        mRawBody = raw;
        mCallback = ProgressInterceptor.getCallback(url);
        mBreakPoint = breakPoint;
        if (mCallback != null) {
            // 断点续传时是剩余文件的长度
            // contentLength() + mBreakPoint 是文件的总长度
            mCallback.onPreExecute(contentLength());
        }
    }

    @Override
    public MediaType contentType() {
        return mRawBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRawBody.contentLength();
    }

    @Override
    public BufferedSource source() throws IOException {
        if (mSource == null) {
            mSource = Okio.buffer(new ProgressSource(mRawBody.source(), contentLength()));
        }
        return mSource;
    }

    private final class ProgressSource extends ForwardingSource {

        final long mContentLength;
        long bytesTotalRead;
        float currentProgress;

        ProgressSource(Source delegate, long remaining) {
            super(delegate);
            bytesTotalRead = mBreakPoint;
            mContentLength = remaining + mBreakPoint;
        }

        @Override
        public long read(@NonNull Buffer sink, long byteCount) throws IOException {
            final long bytesRead = super.read(sink, byteCount);
            if (bytesRead == -1) {
                bytesTotalRead = mContentLength;
            } else {
                bytesTotalRead += bytesRead;
            }
            float progress = 100f * bytesTotalRead / mContentLength;
            // Log.d("ProgressResponseBody", "progress is " + progress + ", total read is "
            //         + bytesTotalRead + ", total is " + mContentLength);
            if (mCallback != null && currentProgress != progress) {
                mCallback.onProgress(progress, bytesTotalRead);
            }
            if (mCallback != null && bytesTotalRead == mContentLength) {
                mCallback = null;
            }
            currentProgress = progress;
            return bytesRead;
        }
    }
}
