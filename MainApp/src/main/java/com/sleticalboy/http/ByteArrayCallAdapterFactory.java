package com.sleticalboy.http;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created on 21-3-19.
 *
 * @author bnli@grandstream.cn
 */
public class ByteArrayCallAdapterFactory extends CallAdapter.Factory {

    public static ByteArrayCallAdapterFactory create() {
        return new ByteArrayCallAdapterFactory();
    }

    private ByteArrayCallAdapterFactory() {
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        if (rawType != byte[].class) return null;
        return new CallAdapter<String, byte[]>() {
            @Override
            public Type responseType() {
                return byte[].class;
            }

            @Override
            public byte[] adapt(Call<String> call) {
                try {
                    String body = call.execute().body();
                    return body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8);
                } catch (IOException e) {
                    return new byte[0];
                }
            }
        };
    }
}
