package com.sleticalboy.dailywork.http.interceptor;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 18-4-3.
 *
 * @author leebin
 * @description 网络拦截器
 */
public final class NetworkInterceptor extends InterceptorImpl {

    public NetworkInterceptor(int level) {
        super(level);
    }

    @Override
    protected void handleRequest(Request request) {

    }

    @Override
    protected void handleResponse(Response response) {

    }
}
