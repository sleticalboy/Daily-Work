package com.sleticalboy.http.builder;

public final class PatchBuilder extends RequestBuilder {
    
    @Override
    protected void realMethod() {
        mRequestBuilder.method("PATCH", mBodyBuilder.build());
    }
}
