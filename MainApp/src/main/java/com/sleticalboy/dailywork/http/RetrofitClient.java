package com.sleticalboy.dailywork.http;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 18-3-26.
 *
 * @author sleticalboy
 * @description
 */
public class RetrofitClient {

    private final OkHttpClient mOkHttpClient;
    private final Retrofit mRetrofit;

    private RetrofitClient() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor) // 打印网络请求日志
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIVE_HOST)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mOkHttpClient)
                .build();
    }

    public static RetrofitClient getInstance() {
        return Holder.RETROFIT_CLIENT;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public <T> T create(Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null");
        }
        return mRetrofit.create(service);
    }

    private static final class Holder {
        private static final RetrofitClient RETROFIT_CLIENT = new RetrofitClient();
    }
}
