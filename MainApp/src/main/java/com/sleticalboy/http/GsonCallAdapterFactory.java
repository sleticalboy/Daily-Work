package com.sleticalboy.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created on 21-3-19.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class GsonCallAdapterFactory extends CallAdapter.Factory {

    private final Gson mGson = new Gson();

    public static CallAdapter.Factory create() {
        return new GsonCallAdapterFactory();
    }

    private GsonCallAdapterFactory() {
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);
        Log.d("GsonCallAdapterFactory", "rawType:" + rawType);
        return new CallAdapter<String, Object>() {
            @Override
            public Type responseType() {
                return returnType;
            }

            @Override
            public Object adapt(Call<String> call) {
                try {
                    TypeAdapter<?> adapter = mGson.getAdapter(rawType);
                    return adapter.fromJson(call.execute().body());
                } catch (IOException e) {
                    return null;
                }
            }
        };
    }
}
