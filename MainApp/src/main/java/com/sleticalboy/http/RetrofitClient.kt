package com.sleticalboy.http

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created on 18-3-26.
 *
 * @author leebin
 * @description
 */
class RetrofitClient private constructor() {

    private val okHttpClient: OkHttpClient
    private val mRetrofit: Retrofit

    fun <T> create(service: Class<T>?): T {
        if (service == null) {
            throw RuntimeException("Api service is null")
        }
        return mRetrofit.create(service)
    }

    private object Holder {
        val client = RetrofitClient()
    }

    companion object {
        fun get(): RetrofitClient = Holder.client
    }

    init {
        val loggingInterceptor = HttpLogInterceptor()
                .setLevel(HttpLogInterceptor.Level.BODY)
        okHttpClient = OkHttpClient.Builder()
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
                .build()
        mRetrofit = Retrofit.Builder()
                .baseUrl(Constants.Companion.LIVE_HOST)
                .addConverterFactory(StringConvertFactory.Companion.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }
}