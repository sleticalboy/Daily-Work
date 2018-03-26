package com.sleticalboy.dailywork.http;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

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
        mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)) // 打印网络请求日志
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIVE_HOST)
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
