package com.sleticalboy.http;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 21-3-20.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class RewriteUrlInterceptor implements Interceptor {

    private static final String TAG = "RewriteUrl";

    static {
        Log.d(TAG, "first static init");
    }

    private static final Object sObj = new Object();
    static {
        Log.d(TAG, "sObj: " + sObj);
    }

    private static Object sObj2 = new Object();
    static {
        Log.d(TAG, "sObj2: " + sObj2);
    }

    private static int sCounter = 0;

    public RewriteUrlInterceptor() {
        Log.d(TAG, "RewriteUrlInterceptor() called");
        new Thread(() -> {
            while (sCounter <= 100) {
                synchronized (sObj) {
                    Log.d(TAG, "Thread A -> produce A");
                    sCounter++;
                    waitOn();
                }
            }
        }).start();
        new Thread(() -> {
            while (sCounter <= 100) {
                synchronized (sObj) {
                    Log.d(TAG, "Thread B -> produce B");
                    sCounter++;
                    sObj.notify();
                }
            }
        }).start();
    }

    private void waitOn() {
        try {
            sObj.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request rawRequest = chain.request();
        List<String> newHost = rawRequest.headers(Constants.REWRITE_HOST);
        if (newHost.size() == 0) return chain.proceed(rawRequest);
        HttpUrl url = HttpUrl.parse(newHost.get(0));
        if (url == null) return chain.proceed(rawRequest);
        Log.v("RewriteUrl", "intercept() new url: " + url + ", host: " + url.host());
        Request.Builder newBuilder = rawRequest.newBuilder();
        newBuilder.removeHeader(Constants.REWRITE_HOST);
        url = rawRequest.url().newBuilder().host(url.host()).build();
        return chain.proceed(newBuilder.url(url).build());
    }
}
