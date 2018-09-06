package com.sleticalboy.okhttp25.http.builder;

import android.support.annotation.NonNull;

import com.sleticalboy.okhttp25.ContextProvider;
import com.sleticalboy.okhttp25.R;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public abstract class RequestBuilder {

    static final String TYPE_TEXT = "text";
    static final String TYPE_IMAGE = "image";
    static final String TYPE_AUDIO = "audio";
    static final String TYPE_VIDEO = "video";
    static final String TYPE_APPLICATION = "application";
    static final RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);
    final Request.Builder mRequestBuilder;
    private long mStartPoint;

    RequestBuilder() {
        mRequestBuilder = new Request.Builder();
    }

    public RequestBuilder url(@NonNull String url) {
        mRequestBuilder.url(inspectUrl(url));
        return this;
    }

    private String inspectUrl(@NonNull String url) {
        ContextProvider.getApplicationContext().getResources().getString(R.string.app_name);
        return url;
    }

    public Request build() {
        method();
        return mRequestBuilder.build();
    }

    protected abstract RequestBuilder method();

    public RequestBuilder header(@NonNull String name, @NonNull String value) {
        mRequestBuilder.header(name, value);
        return this;
    }

    public RequestBuilder headers(@NonNull Map<String, String> headers) {
        if (headers.size() != 0) {
            for (final String key : headers.keySet()) {
                mRequestBuilder.header(key, headers.get(key));
            }
        }
        return this;
    }

    /**
     * 注入 header
     *
     * @param name  key
     * @param value value
     * @return {@link RequestBuilder}
     */
    public RequestBuilder injectHeader(@NonNull String name, @NonNull String value) {
        return header(name, value);
    }

    public RequestBuilder injectHeaders(@NonNull Map<String, String> headers) {
        return headers(headers);
    }

    public RequestBuilder delete(Map<String, String> params) {
        return this;
    }

    public RequestBuilder post(@NonNull Map<String, String> params, List<String> files) {
        return this;
    }

    public RequestBuilder put(@NonNull Map<String, String> params, List<String> files) {
        return this;
    }

    public RequestBuilder breakPoint(long startPoint) {
        return this;
    }
}
