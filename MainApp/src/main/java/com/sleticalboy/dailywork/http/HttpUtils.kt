package com.sleticalboy.dailywork.http

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by Android Studio.
 * Date: 12/30/17.
 *
 * @author sleticalboy
 */
object HttpUtils {

    private val mOkHttpClient = RetrofitClient.getInstance().okHttpClient

    private fun isEmpty(text: CharSequence?): Boolean {
        return text == null || text.isEmpty() || text.toString().trim { it <= ' ' }.isEmpty()
    }

    /**
     * 普通的 get 请求
     */
    fun request(completeUrl: String, callback: okhttp3.Callback?) {
        if (isEmpty(completeUrl) || callback == null)
            throw IllegalArgumentException("completeUrl or callback is null")

        val request = Request.Builder()
                .get()
                .url(completeUrl)
                .build()

        mOkHttpClient.newCall(request).enqueue(callback)
    }

    /**
     * key value 形式的 post请求
     */
    fun request(completeUrl: String, requestMap: Map<String, Any>, callback: okhttp3.Callback?) {
        if (isEmpty(completeUrl) || callback == null)
            throw IllegalArgumentException("completeUrl or callback is null")
        val builder = StringBuffer("?")
        if (requestMap.isNotEmpty()) {
            for (entry in requestMap.iterator()) {
                builder.append(entry.key).append("=").append(entry.value).append("&")
            }
        }
        builder.replace(builder.lastIndexOf("&"), builder.length, "")
        val body = RequestBody.create(MediaType.parse("application/json"), builder.toString())
        val request = Request.Builder().post(body)
                .url(completeUrl)
                .build()
        mOkHttpClient.newCall(request).enqueue(callback)
    }
}
