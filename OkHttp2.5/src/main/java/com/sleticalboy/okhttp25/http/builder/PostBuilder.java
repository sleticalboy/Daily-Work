package com.sleticalboy.okhttp25.http.builder;

import android.support.annotation.NonNull;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class PostBuilder extends AbstractBuilder {

    private final MultipartBuilder mBuilder = new MultipartBuilder();

    @Override
    protected AbstractBuilder method() {
        mRequestBuilder.post(createBody());
        return this;
    }

    private RequestBody createBody() {
        return mBuilder.build();
    }

    public void post(@NonNull Map<String, String> params, List<String> files) {
        if (params.size() != 0) {
            for (final String key : params.keySet()) {
                mBuilder.addFormDataPart(key, params.get(key));
            }
        }
        if (files != null && files.size() != 0) {
            for (final String path : files) {
                final File file = new File(path);
                if (!file.exists()) {
                    return;
                }
                mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse("*/*"), file));
            }
        }
    }
}
