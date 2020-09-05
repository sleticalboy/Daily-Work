package com.sleticalboy.http

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Created on 18-3-26.
 *
 * @author leebin
 * @description
 */
class StringConvertFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                       retrofit: Retrofit)
            : Converter<ResponseBody, String>? {
        return Converter { value: ResponseBody -> value.string() }
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit)
            : Converter<String, RequestBody>? {
        return Converter { value: String? -> RequestBody.create(MediaType.parse(JSON), value) }
    }

    companion object {

        private const val JSON = "application/json; charset=UTF-8"

        fun create(): StringConvertFactory {
            return StringConvertFactory()
        }
    }
}