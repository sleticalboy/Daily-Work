package com.binlee.emoji.third;

import androidx.annotation.NonNull;

import com.binlee.emoji.compat.HttpEngine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class OkHttpEngine extends HttpEngine<Request, Response> {

    private volatile OkHttpClient mClient;
    private Config mConfig;

    private OkHttpClient client() {
        if (mClient == null) {
            synchronized (this) {
                if (mClient == null) {
                    setupClient(null);
                }
            }
        }
        return mClient;
    }

    @Override
    protected void setupClient(Object client) {
        mConfig = createConfig();
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(mConfig.connectTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(mConfig.writeTimeout, TimeUnit.SECONDS);
        builder.readTimeout(mConfig.readTimeout, TimeUnit.SECONDS);
        builder.proxy(mConfig.proxy);
        final Authenticator authenticator = mConfig.authenticate == null
                ? Authenticator.NONE
                : (route, response) -> adaptRequest(mConfig.authenticate);
        builder.authenticator(authenticator);
        mClient = builder.build();
    }

    @Override
    public BaseResponse request(@NonNull BaseRequest request) throws IOException {
        final okhttp3.Response raw = client().newCall(adaptRequest(request)).execute();
        final BaseResponse response = adaptResponse(raw, request);
        trace("OkHttpEngine", response);
        return response;
    }

    @Override
    public void request(@NonNull BaseRequest request, @NonNull Callback callback) {
        client().newCall(adaptRequest(request)).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler().post(() -> callback.onFailure(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response raw)
                    throws IOException {
                final BaseResponse response = adaptResponse(raw, request);
                mainHandler().post(() -> callback.onResponse(response));
                trace("OkHttpEngine", response);
            }
        });
    }

    @Override
    protected okhttp3.Request adaptRequest(BaseRequest request) {
        final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.url(buildUrl(request));
        builder.headers(toHeaders(request.headers));
        if (GET.equals(request.method)) {
            builder.get();
        } else if (POST.equals(request.method)) {
            builder.post(toRequestBody(request));
        } else if (PUT.equals(request.method)) {
            builder.put(toRequestBody(request));
        } else if (DELETE.equals(request.method)) {
            builder.delete();
        }
        return builder.build();
    }

    private RequestBody toRequestBody(BaseRequest request) {
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String name : request.params.keySet()) {
            final Object value = request.params.get(name);
            if (value instanceof File) {
                // type File
                builder.addPart(MultipartBody.Part.createFormData("files",
                        ((File) value).getName(),
                        RequestBody.create(null, ((File) value)))
                );
            } else {
                // type String
                builder.addPart(MultipartBody.Part.createFormData(name, String.valueOf(value)));
            }
        }
        return builder.build();
    }

    private Headers toHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (mConfig.userAgent != null) {
            headers.put("User-Agent", mConfig.userAgent);
        }
        return Headers.of(headers);
    }

    @Override
    protected BaseResponse adaptResponse(okhttp3.Response raw, BaseRequest current) throws IOException {
        final BaseResponse response = new BaseResponse();
        response.protocol = raw.protocol().toString();
        response.code = raw.code();
        response.msg = raw.message();
        response.headers = toHeaders(raw.headers());
        if (raw.body() != null) {
            response.data = raw.body().bytes();
        }
        response.current = current;
        return response;
    }

    private Map<String, String> toHeaders(Headers headers) {
        final Map<String, String> ret = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            ret.put(headers.name(i), headers.value(i));
        }
        return ret;
    }
}
