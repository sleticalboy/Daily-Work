package com.sleticalboy.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created on 18-3-26.
 *
 * @author leebin
 * @description
 */
public final class StringConvertFactory extends Converter.Factory {

    private static final String JSON = "application/json; charset=UTF-8";

    public static StringConvertFactory create() {
        return new StringConvertFactory();
    }

    @Override
    public Converter<ResponseBody, String> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        return value -> value.string();
    }

    @Override
    public Converter<String, RequestBody> requestBodyConverter(
            Type type, Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations, Retrofit retrofit) {
        return value -> RequestBody.create(MediaType.parse(JSON), value);
    }
}
