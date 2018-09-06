package com.sleticalboy.okhttp25.http.builder;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class GetBuilder extends RequestBuilder {

    @Override
    protected RequestBuilder method() {
        mRequestBuilder.get();
        return this;
    }

    /**
     * 用于断点续传
     *
     * @param startPoint 断点的位置
     */
    @Override
    public RequestBuilder breakPoint(long startPoint) {
        mRequestBuilder.header("RANGE", "bytes=" + startPoint + "-");
        return this;
    }

}
