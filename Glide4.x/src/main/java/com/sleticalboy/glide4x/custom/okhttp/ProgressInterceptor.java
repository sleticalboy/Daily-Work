package com.sleticalboy.glide4x.custom.okhttp;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import android.util.Log;

import com.sleticalboy.glide4x.listener.ProgressListener;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 * @description
 */
public class ProgressInterceptor implements Interceptor {

    private static final Map<String, ProgressListener> PROCESS_INTERCEPTOR_MAP =
            new ArrayMap<>();

    public static void addListener(String tag, ProgressListener progressListener) {
        PROCESS_INTERCEPTOR_MAP.put(tag, progressListener);
    }

    public static void removeListener(String tag) {
        if (PROCESS_INTERCEPTOR_MAP.containsKey(tag)) {
            PROCESS_INTERCEPTOR_MAP.remove(tag);
        }
    }

    public static ProgressListener getListener(String tag) {
        return PROCESS_INTERCEPTOR_MAP.get(tag);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request request = chain.request();
        final Response response = chain.proceed(request);
        final String url = request.url().toString();
        Log.d("ProgressInterceptor", url);
        final ResponseBody body = response.body();
        return response.newBuilder()
                .body(new ProgressResponseBody(url, body))
                .build();
    }
}
