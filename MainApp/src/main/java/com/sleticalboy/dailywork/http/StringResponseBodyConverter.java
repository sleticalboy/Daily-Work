package com.sleticalboy.dailywork.http;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created on 18-3-26.
 *
 * @author sleticalboy
 * @description
 */
public final class StringResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    public StringResponseBodyConverter() {
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        return (T) value.string();
    }
}
