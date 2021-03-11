package com.sleticalboy.glide4x.custom.okhttp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.sleticalboy.glide4x.listener.ProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 */
public class ProgressResponseBody extends ResponseBody {

    private static final String TAG = "ProgressResponseBody";
    private final ResponseBody mBody;
    private BufferedSource mSource;
    private ProgressListener mListener;

    public ProgressResponseBody(ProgressListener listener, ResponseBody body) {
        mBody = body;
        mListener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() {
        return mBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mSource == null) {
            mSource = Okio.buffer(new ProgressSource(mBody.source()));
        }
        return mSource;
    }

    class ProgressSource extends ForwardingSource {

        long mTotalByteRead = 0L;
        int mCurrentProgress;

        ProgressSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(@NonNull Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            long fullLength = mBody.contentLength();
            if (bytesRead == -1) {
                mTotalByteRead = fullLength;
            } else {
                mTotalByteRead += bytesRead;
            }
            int progress = (int) (100f * mTotalByteRead / fullLength);
            Log.d(TAG, "download progress is " + progress);
            if (mListener != null && progress != mCurrentProgress) {
                mListener.onProgress(progress);
            }
            if (mListener != null && mTotalByteRead == fullLength) {
                mListener = null;
            }
            mCurrentProgress = progress;
            return bytesRead;
        }
    }
}
