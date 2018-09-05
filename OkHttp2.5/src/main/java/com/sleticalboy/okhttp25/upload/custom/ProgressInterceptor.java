package com.sleticalboy.okhttp25.upload.custom;

import android.support.v4.util.ArrayMap;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public class ProgressInterceptor implements Interceptor {

    private static final Map<String, ProgressCallback> PROGRESS_CALLBACK_MAP = new ArrayMap<>();
    private long mBreakPoint;

    public static ProgressInterceptor newInstance() {
        return new ProgressInterceptor();
    }

    public static void addCallback(String tag, ProgressCallback callback) {
        PROGRESS_CALLBACK_MAP.put(tag, callback);
    }

    static ProgressCallback getCallback(String tag) {
        return PROGRESS_CALLBACK_MAP.get(tag);
    }

    public static void removeCallback(String tag) {
        if (PROGRESS_CALLBACK_MAP.containsKey(tag)) {
            PROGRESS_CALLBACK_MAP.remove(tag);
        }
    }

    public static void removeAll() {
        PROGRESS_CALLBACK_MAP.clear();
    }

    public void setBreakPoint(long breakPoint) {
        mBreakPoint = breakPoint;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        final String url = request.url().toString();
        final ResponseBody body = response.body();
        return response.newBuilder()
                .body(new ProgressResponseBody(url, body, mBreakPoint))
                .build();
    }
}
