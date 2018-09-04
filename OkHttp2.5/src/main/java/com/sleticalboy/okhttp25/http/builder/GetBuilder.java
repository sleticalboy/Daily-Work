package com.sleticalboy.okhttp25.http.builder;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class GetBuilder extends AbstractBuilder {

    @Override
    protected AbstractBuilder method() {
        mRequestBuilder.get();
        return this;
    }
}
