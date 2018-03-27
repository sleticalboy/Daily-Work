package com.sleticalboy.dailywork.http;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created on 18-3-26.
 *
 * @author sleticalboy
 * @description
 */
public class StringRequestBodyConverter<T> implements Converter<T, RequestBody> {

    public StringRequestBodyConverter() {
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), value.toString());
    }
}
