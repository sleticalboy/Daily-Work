package com.sleticalboy.learning.data;

import androidx.annotation.NonNull;

/**
 * Created on 20-3-31.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class Result<T> {

    public static class Success<Data> extends Result<Data> {

        private final Data mData;

        public Success(final Data data) {
            mData = data;
        }

        public Data getData() {
            return mData;
        }
    }

    public static class Error extends Result {

        private final Throwable mCause;

        public Error(final Throwable cause) {
            mCause = cause;
        }

        public Throwable getCause() {
            return mCause;
        }
    }

    public static class Loading extends Result {
    }

    @NonNull
    @Override
    public String toString() {
        if (this instanceof Success) {
            return "data = " + ((Success<T>) this).getData();
        }
        if (this instanceof Error) {
            return "error = " + ((Error) this).getCause();
        }
        if (this instanceof Loading) {
            return "Loading...";
        }
        return super.toString();
    }
}
