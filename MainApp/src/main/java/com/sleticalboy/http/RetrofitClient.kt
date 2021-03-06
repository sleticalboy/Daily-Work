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
        if (service == null) throw RuntimeException("Api service is null")
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
                .addInterceptor(loggingInterceptor) // 打印网络请求日志
                .addInterceptor(RewriteUrlInterceptor()) // 重写 url，替换 host
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                // .cookieJar(object : CookieJar {
                //
                //     private val mCookies = hashMapOf<String, List<Cookie>>()
                //
                //     override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                //         mCookies[url.toString()] = cookies
                //     }
                //
                //     override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                //         val list = mCookies[url.toString()] ?: return arrayListOf()
                //         return list as MutableList<Cookie>
                //     }
                // })
                // .cookieJar(CookieJarImpl(null))
                .build()
        mRetrofit = Retrofit.Builder()
                .baseUrl(Constants.HOST_FAKE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                // .callFactory { request ->
                //     // 可以考虑在这里动态替换 url，当然也可以在拦截器中替换
                //     okHttpClient.newCall(request)
                // }
                .build()
    }
}