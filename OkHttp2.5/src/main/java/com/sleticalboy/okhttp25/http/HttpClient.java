package com.sleticalboy.okhttp25.http;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.sleticalboy.okhttp25.http.builder.DeleteBuilder;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.sleticalboy.okhttp25.http.builder.PostBuilder;
import com.sleticalboy.okhttp25.http.builder.PutBuilder;
import com.sleticalboy.okhttp25.http.builder.RequestBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created on 18-9-3.
 *
 * @author leebin
 */
public final class HttpClient {
    
    private static final boolean DBG = true;
    private OkHttpClient mOkHttpClient;
    private Handler mMainHandler;
    private boolean mInitialized = false;
    // private boolean vpnEnable = false;
    
    private HttpClient() {
        internalInit();
    }
    
    private void internalInit() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(10, TimeUnit.MINUTES);
        mOkHttpClient.setReadTimeout(10, TimeUnit.MINUTES);
        mOkHttpClient.setWriteTimeout(10, TimeUnit.MINUTES);
        
        /*Route#
        public boolean requiresTunnel() {
            return address.sslSocketFactory != null && proxy.type() == Proxy.Type.HTTP;
        }*/
        // if (vpnEnable) {
        //     mOkHttpClient.setSslSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
        //     final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8088));
        //     mOkHttpClient.setProxy(proxy);
        //     mOkHttpClient.setProxySelector(ProxySelector.getDefault());
        // }
        
        if (DBG) {
            final HttpLoggerInterceptor loggerInterceptor = new HttpLoggerInterceptor()
                    .setLevel(HttpLoggerInterceptor.Level.HEADERS);
            mOkHttpClient.interceptors().add(loggerInterceptor);
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
    public HttpClient addInterceptor(Interceptor interceptor) {
        interceptors().add(interceptor);
        return this;
    }
    
    public List<Interceptor> interceptors() {
        checkInitialize();
        return mOkHttpClient.interceptors();
    }
    
    /**
     * 执行异步网络请求
     *
     * @param builder  {@link RequestBuilder} see also {@link GetBuilder}, {@link PostBuilder},
     *                 {@link PutBuilder}, {@link DeleteBuilder}
     * @param async    是否异步
     * @param callback {@link HttpCallback} 网络请求回调[都已经回调到了主线程]
     * @param <T>      请求返回类型
     */
    public <T> void execute(@NonNull RequestBuilder builder, boolean async, HttpCallback<T> callback) {
        checkInitialize();
        final Call newCall = mOkHttpClient.newCall(builder.build());
        if (async) {
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
                            Object object;
                            if (callback.isStringType()) {
                                object = result;
                            } else {
                                object = JSON.parseObject(result, callback.getGenericClass());
                            }
                            mMainHandler.post(() -> callback.onSuccess((T) object));
                        }
                    }
                    if (!newCall.isCanceled()) {
                        newCall.cancel();
                    }
                }
            });
        } else {
            try {
                Response response = newCall.execute();
                if (response != null && response.isSuccessful()) {
                    final String result = response.body().string();
                    if (callback != null) {
                        Object object;
                        if (callback.isStringType()) {
                            object = result;
                        } else {
                            object = JSON.parseObject(result, callback.getGenericClass());
                        }
                        callback.onSuccess((T) object);
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(new IOException("IOException"));
                    }
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        }
    }
    
    /**
     * 执行异步网络请求
     *
     * @param builder  {@link RequestBuilder} see also {@link GetBuilder}, {@link PostBuilder},
     *                 {@link PutBuilder}, {@link DeleteBuilder}
     * @param callback {@link HttpCallback} 网络请求回调[都已经回调到了主线程]
     * @param <T>      请求返回类型
     */
    public <T> void asyncExecute(@NonNull RequestBuilder builder, HttpCallback<T> callback) {
        execute(builder, true, callback);
    }
    
    public Handler getMainHandler() {
        checkInitialize();
        return mMainHandler;
    }
    
    private final static class SingleHolder {
        private static final HttpClient HTTP_CLIENT = new HttpClient();
    }
    
}
