package com.sleticalboy.okhttp25.upload.custom;

import android.support.annotation.NonNull;
import android.util.Log;

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
 * @author sleticalboy
 */
public final class ProgressResponseBody extends ResponseBody {

    private final ResponseBody mResponseBody;
    private ProgressCallback mCallback;
    private BufferedSource mSource;

    public ProgressResponseBody(String url, ResponseBody body) {
        mResponseBody = body;
        mCallback = ProgressInterceptor.getCallback(url);
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() throws IOException {
        if (mSource == null) {
            mSource = Okio.buffer(new ProgressSource(mResponseBody.source()));
        }
        return mSource;
    }

    private class ProgressSource extends ForwardingSource {

        long bytesTotalRead;
        int currentProgress;

        ProgressSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(@NonNull Buffer sink, long byteCount) throws IOException {
            final long bytesRead = super.read(sink, byteCount);
            final long contentLength = mResponseBody.contentLength();
            if (bytesRead == -1) {
                bytesTotalRead = contentLength;
            } else {
                bytesTotalRead += bytesRead;
            }
            int progress = (int) (100F * bytesTotalRead / contentLength);
//            Log.d("ProgressResponseBody", "progress is " + progress + ", total read is " + bytesTotalRead);
            if (mCallback != null && currentProgress != progress) {
                mCallback.onProgress(progress, bytesTotalRead);
            }
            if (mCallback != null && bytesTotalRead == contentLength) {
                mCallback = null;
            }
            currentProgress = progress;
            return bytesRead;
        }
    }
}
