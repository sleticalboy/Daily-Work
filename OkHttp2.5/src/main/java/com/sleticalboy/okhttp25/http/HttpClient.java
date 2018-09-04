package com.sleticalboy.okhttp25.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.sleticalboy.okhttp25.ContextProvider;
import com.sleticalboy.okhttp25.R;
import com.sleticalboy.okhttp25.http.builder.AbstractBuilder;
import com.sleticalboy.okhttp25.http.builder.DeleteBuilder;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.sleticalboy.okhttp25.http.builder.PostBuilder;
import com.sleticalboy.okhttp25.http.builder.PutBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class HttpClient {

    private static final boolean DBG = true;
    private OkHttpClient mOkHttpClient;
    private Handler mMainHandler;
    private boolean mInitialized = false;
    //    private String mBaseUrl;

    private HttpClient() {
        internalInit();
    }

    private void internalInit() {
        mMainHandler = new Handler(Looper.getMainLooper());
//        mBaseUrl = ContextProvider.getApplicationContext().getResources().getString(R.string.app_name);
        mOkHttpClient = new OkHttpClient();
        if (DBG) {
            mOkHttpClient.interceptors().add(new HttpLoggerInterceptor());
        }
        mInitialized = true;
    }

    public static HttpClient getInstance() {
        return SingleHolder.HTTP_CLIENT;
    }

    public OkHttpClient getOkHttpClient() {
        checkInitialize();
        return mOkHttpClient;
    }

    private void checkInitialize() {
        if (!mInitialized) {
            internalInit();
        }
    }

    /**
     * 添加拦截器
     *
     * @param interceptor {@link Interceptor}
     * @return {@link HttpClient}
     */
    public HttpClient interceptor(Interceptor interceptor) {
        checkInitialize();
        mOkHttpClient.interceptors().add(interceptor);
        return this;
    }

    /**
     * 执行异步网络请求
     *
     * @param builder  {@link AbstractBuilder} see also {@link GetBuilder}, {@link PostBuilder},
     *                 {@link PutBuilder}, {@link DeleteBuilder}
     * @param callback {@link HttpCallback} 网络请求回调[都已经回调到了主线程]
     * @param <T>      请求返回类型
     */
    public <T> void asyncExecute(@NonNull AbstractBuilder builder, HttpCallback<T> callback) {
        checkInitialize();
        final Call newCall = mOkHttpClient.newCall(builder.build());
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (callback != null) {
                    mMainHandler.post(() -> callback.onFailure(e));
                }
                if (!newCall.isCanceled()) {
                    newCall.cancel();
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onResponse(Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    final String result = response.body().string();
                    if (callback != null) {
                        final Class<T> genericClass = callback.getGenericClass();
                        Object object;
                        if (genericClass != String.class) {
                            object = JSON.parseObject(result, genericClass);
                        } else {
                            object = result;
                        }
                        mMainHandler.post(() -> callback.onSuccess((T) object));
                    }
                }
                if (!newCall.isCanceled()) {
                    newCall.cancel();
                }
            }
        });
    }

    public void download(@NonNull AbstractBuilder builder, HttpCallback<Response> callback) {
        checkInitialize();
    }

    public Handler getMainHandler() {
        checkInitialize();
        return mMainHandler;
    }

    private final static class SingleHolder {
        private static final HttpClient HTTP_CLIENT = new HttpClient();
    }

}
