package com.sleticalboy.dailywork.http.interceptor;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created on 18-4-3.
 *
 * @author sleticalboy
 * @description 日志拦截器
 */
public final class LogInterceptor extends InterceptorImpl {

    public LogInterceptor(int level) {
        super(level);
    }

    @Override
    protected void handleRequest(Request request) {
        switch (getLevel()) {
            default:
            case LEVEL_NONE:
                break;
            case LEVEL_BASIC:
                break;
            case LEVEL_HEADER:
                break;
            case LEVEL_BODY:
                break;
        }
        mLogger.log(request.headers().toString());
    }

    @Override
    protected void handleResponse(Response response) {
        mLogger.log(response.headers().toString());
    }
}
