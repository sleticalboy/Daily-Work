package com.binlee.emoji.compat;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.binlee.emoji.BuildConfig;
import com.binlee.emoji.helper.LogHelper;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpEngine<RealRequest, RealResponse> {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private volatile Handler mHandler;

    protected abstract void setupClient(Object client);

    /**
     * 发送请求：同步
     *
     * @param request 请求
     * @return 响应
     */
    public abstract BaseResponse request(@NonNull BaseRequest request) throws IOException;

    /**
     * 发送请求：异步
     *
     * @param request  请求
     * @param callback 回调
     */
    public abstract void request(@NonNull BaseRequest request, @NonNull Callback callback);

    /**
     * @param request 通用的 {@link BaseRequest}
     * @return 实际的 http 请求
     * @throws IOException io 异常
     */
    protected abstract RealRequest adaptRequest(BaseRequest request) throws IOException;

    /**
     * @param raw  实际的 http 响应
     * @param current 本次响应对应的 {@link BaseRequest}
     * @return 通用的 {@link BaseResponse}
     * @throws IOException io 异常
     */
    protected abstract BaseResponse adaptResponse(RealResponse raw, BaseRequest current) throws IOException;

    protected Handler mainHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    protected boolean trackable() {
        return BuildConfig.DEBUG;
    }

    protected Config createConfig() {
        return new Config();
    }

    protected void trace(String tag, BaseResponse response) {
        if (!trackable()) {
            return;
        }
        // trace request
        final BaseRequest request = response.current;
        final StringBuilder buffer = new StringBuilder(">>>>>>>>>>>>>>>traceRequest:\n");
        // request line
        buffer.append(request.method).append(" ").append(request.url).append("\n\n");
        // request headers
        if (request.headers != null) {
            for (String name : request.headers.keySet()) {
                buffer.append(name).append(": ").append(request.headers.get(name)).append('\n');
            }
            buffer.append("\n");
        }
        // params
        if (request.params != null) {
            for (String name : request.params.keySet()) {
                buffer.append(name).append('=').append(request.params.get(name)).append('\n');
            }
        }
        // traceResponse
        buffer.append("traceResponse:<<<<<<<<<<<<<<\n");
        // status line
        buffer.append(response.protocol).append(" ").append(response.code)
                .append(" ").append(response.msg).append("\n\n");
        // response headers
        if (response.headers != null) {
            for (String name : response.headers.keySet()) {
                buffer.append(name).append(": ").append(response.headers.get(name)).append('\n');
            }
            buffer.append("\n");
        }
        // response body
        if (response.data != null && response.data.length != 0) {
            buffer.append(response.string());
        }
        LogHelper.debug(tag, buffer.toString());
    }

    protected static String buildUrl(BaseRequest request) {
        if (!GET.equals(request.method) || request.params == null || request.params.size() == 0) {
            return request.url;
        }
        request.params.size();
        final StringBuilder builder = new StringBuilder();
        builder.append(request.url);
        if (!request.url.contains("?")) {
            builder.append("?");
        } else {
            builder.append("&");
        }
        int i = 0;
        for (String name : request.params.keySet()) {
            if (i != 0) {
                builder.append("&");
            }
            builder.append(name).append("=").append(request.params.get(name));
            i++;
        }
        return builder.toString();
    }

    public static abstract class Callback {

        public void onProgress(float progress) {
            //
        }

        public abstract void onResponse(BaseResponse response);

        public abstract void onFailure(Throwable e);
    }

    public static class BaseRequest implements Serializable {

        private static final long serialVersionUID = -316276577868557178L;

        public Map<String, String> headers;
        public String url;
        @Method
        public String method;
        public Map<String, Object> params;

        public static BaseRequest base(String url, @Method String method) {
            final BaseRequest request = new BaseRequest();
            request.url = url;
            request.method = method;
            request.headers = new HashMap<>();
            return request;
        }
    }

    public static class BaseResponse implements Serializable {

        private static final long serialVersionUID = -6734972010487266836L;

        public Map<String, String> headers;
        public BaseRequest current;
        public String protocol;
        public int code;
        public String msg;
        public byte[] data;

        public boolean success() {
            return code / 100 == 2;
        }

        public boolean redirect() {
            return code / 100 == 3;
        }

        public boolean clientError() {
            return code / 100 == 4;
        }

        public boolean serverError() {
            return code / 100 == 5;
        }

        public byte[] bytes() {
            return data == null || data.length == 0 ? msg.getBytes() : data;
        }

        public String string() {
            return new String(bytes());
        }
    }

    public static class Config {
        /**
         * 超时时间：单位 s
         */
        public int connectTimeout = 60;
        public int writeTimeout = 60;
        public int readTimeout = 60;

        public java.net.Proxy proxy;
        public BaseRequest authenticate;
        // "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36"
        public String userAgent;
    }

    @StringDef({GET, POST, PUT, DELETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Method {
    }
}
