package com.sleticalboy.http.builder;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class PutBuilder extends RequestBuilder {
    
    @Override
    protected void realMethod() {
        mRequestBuilder.method("PUT", mBodyBuilder.build());
    }
}
