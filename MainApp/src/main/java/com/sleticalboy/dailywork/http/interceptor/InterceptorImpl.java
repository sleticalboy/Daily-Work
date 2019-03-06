package com.sleticalboy.dailywork.http.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;

/**
 * Created on 18-4-3.
 *
 * @author leebin
 * @description
 */
public abstract class InterceptorImpl implements Interceptor {

    public static final int LEVEL_NONE = 678; // 不输出任何日志
    public static final int LEVEL_BASIC = 677; // 只输出基本的日志
    public static final int LEVEL_HEADER = 676; // 只输出请求头
    public static final int LEVEL_BODY = 675; // 输出请求头和请求体
    protected final Logger mLogger;
    private final int mLevel;

    public InterceptorImpl(int level) {
        mLogger = Logger.DEFAULT;
        mLevel = level;
    }

    @Override
    public final Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        handleRequest(request);
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            mLogger.log("<-- HTTP FAILED: " + e);
            throw e;
        }
        handleResponse(response);
        return response;
    }

    protected int getLevel() {
        return mLevel;
    }

    protected abstract void handleRequest(Request request);

    protected abstract void handleResponse(Response response);

    public interface Logger {

        Logger DEFAULT = new Logger() {
            @Override
            public void log(String msg) {
                log(msg, null);
            }

            @Override
            public void log(String msg, Throwable t) {
                Platform.get().log(Platform.INFO, msg, t);
            }
        };

        void log(String msg);

        void log(String msg, Throwable t);
    }
}
