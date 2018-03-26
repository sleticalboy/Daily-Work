package com.sleticalboy.dailywork.http

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Android Studio.
 * Date: 12/30/17.
 *
 * @author sleticalboy
 */
object HttpUtils {

    private val mOkHttpClient = RetrofitClient.getInstance().okHttpClient
// OkHttpClient.Builder()
//            .addNetworkInterceptor(HttpLoggingInterceptor()
//                    .setLevel(HttpLoggingInterceptor.Level.BODY)) // 打印网络请求日志
//            .build()

    private fun isEmpty(text: CharSequence?): Boolean {
        return text == null || text.isEmpty() || text.toString().trim { it <= ' ' }.isEmpty()
    }

    fun request(completeUrl: String, callback: okhttp3.Callback?) {
        if (isEmpty(completeUrl) || callback == null)
            throw IllegalArgumentException("completeUrl or callback is null")

        val request = Request.Builder()
                .url(completeUrl)
                .build()

        mOkHttpClient.newCall(request).enqueue(callback)
    }
}
