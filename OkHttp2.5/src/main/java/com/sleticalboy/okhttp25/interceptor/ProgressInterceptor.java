package com.sleticalboy.okhttp25.interceptor;

import android.support.v4.util.ArrayMap;

import com.sleticalboy.okhttp25.body.ProgressResponseBody;
import com.sleticalboy.okhttp25.callback.ProgressCallback;
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
public final class ProgressInterceptor implements Interceptor {

    private static final Map<String, ProgressCallback> PROGRESS_CALLBACK_MAP = new ArrayMap<>();

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

    @Override
    public Response intercept(Chain chain) throws IOException {
        // RANGE: bytes=123456-654321
        final Request request = chain.request();
        final String range = request.header("RANGE");
        long breakPoint = 0L;
        // 取出请求头中的断点信息
        if (range != null && range.contains("-")) {
            breakPoint = Long.parseLong(range.split("-")[0]);
        }
        final Response response = chain.proceed(request);
        final ResponseBody body = response.body();
        return response.newBuilder()
                .body(new ProgressResponseBody(request.url().toString(), body, breakPoint))
                .build();
    }
}
