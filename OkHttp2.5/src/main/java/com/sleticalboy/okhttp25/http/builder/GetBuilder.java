package com.sleticalboy.okhttp25.http.builder;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class GetBuilder extends RequestBuilder {
    
    @Override
    protected void method() {
        mRequestBuilder.get();
    }
    
    /**
     * 用于断点续传
     *
     * @param startPoint 断点的位置
     */
    @Override
    public RequestBuilder breakPoint(Long startPoint, Long endPoint) {
        // RANGE: bytes=123456-654321
        // final StringBuilder builder = new StringBuilder();
        // if (startPoint >= 0) {
        //     builder.append("bytes=").append(startPoint).append("-");
        //     if (endPoint >= startPoint) {
        //         builder.append(endPoint);
        //     }
        // }
        // mRequestBuilder.header("RANGE", builder.toString());
        mRequestBuilder.header("RANGE", "bytes=" + startPoint + "-" + (endPoint == null ? "" : endPoint));
        return this;
    }
    
}
