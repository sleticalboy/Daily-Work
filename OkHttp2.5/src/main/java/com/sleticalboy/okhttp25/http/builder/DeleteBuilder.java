package com.sleticalboy.okhttp25.http.builder;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

/**
 * Created on 18-9-3.
 *
 * @author leebin
 */
public final class DeleteBuilder extends RequestBuilder {

    private final MultipartBuilder mBuilder = new MultipartBuilder();
    private boolean mEmpty = false;

    @Override
    protected void method() {
        mRequestBuilder.delete(createBody());
    }

    private RequestBody createBody() {
        return mEmpty ? EMPTY_BODY : mBuilder.build();
    }

    @Override
    public RequestBuilder delete(Map<String, String> params) {
        mEmpty = params == null || params.size() == 0;
        if (!mEmpty) {
            for (final String key : params.keySet()) {
                mBuilder.addFormDataPart(key, params.get(key));
            }
        }
        return this;
    }
}
