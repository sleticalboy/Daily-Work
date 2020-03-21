package com.sleticalboy.http;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 18-3-26.
 *
 * @author leebin
 * @description
 */
public class RetrofitClient {

    private final OkHttpClient mOkHttpClient;
    private final Retrofit mRetrofit;

    private RetrofitClient() {
        final HttpLogInterceptor loggingInterceptor = new HttpLogInterceptor()
                .setLevel(HttpLogInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor) // 打印网络请求日志
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
//                .cookieJar(new CookieJar() { // cookie 持久化[只是在内存中]
//                    private Map<String, List<Cookie>> mCookies = new HashMap<>();
//
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                        mCookies.put(url.host(), cookies);
//                    }
//
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url) {
//                        final List<Cookie> cookies = mCookies.get(url.host());
//                        return cookies != null ? cookies : new ArrayList<>();
//                    }
//                })
//                .cookieJar(new CookieJarImpl(null)) // 本地持久化
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIVE_HOST)
                .addConverterFactory(StringConvertFactory.create())
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
