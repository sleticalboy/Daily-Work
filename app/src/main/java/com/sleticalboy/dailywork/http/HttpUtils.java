package com.sleticalboy.dailywork.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Android Studio.
 * Date: 12/30/17.
 *
 * @author sleticalboy
 */
public class HttpUtils {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)) // 打印网络请求日志
            .build();

    private static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0 || text.toString().trim().length() == 0;
    }

    public static void request(String completeUrl, okhttp3.Callback callback) {
        if (isEmpty(completeUrl) || callback == null)
            throw new IllegalArgumentException("completeUrl or callback is null");

        Request request = new Request.Builder()
                .url(completeUrl)
                .build();

        mOkHttpClient.newCall(request).enqueue(callback);
    }
}
