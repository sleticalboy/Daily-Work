package com.binlee.emoji.compat;

import androidx.annotation.NonNull;

import com.binlee.emoji.helper.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DefaultHttpEngine extends HttpEngine<HttpURLConnection, HttpURLConnection> {

    private final Config mConfig;
    private volatile ExecutorService mService;
    private volatile boolean mReauthenticating = false;
    private volatile int redirectCount;

    public DefaultHttpEngine() {
        mConfig = createConfig();
    }

    private ExecutorService service() {
        if (mService == null) {
            mService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<>(), r -> new Thread(r, "DefaultHttpEngine"));
        }
        return mService;
    }

    @Override
    protected void setupClient(Object client) {
        if (!(client instanceof HttpURLConnection)) {
            return;
        }
        final HttpURLConnection conn = (HttpURLConnection) client;
        conn.setConnectTimeout(mConfig.connectTimeout * 1000);
        conn.setReadTimeout(mConfig.readTimeout * 1000);
        conn.setInstanceFollowRedirects(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
    }

    @Override
    public BaseResponse request(@NonNull BaseRequest request) throws IOException {
        if (mReauthenticating) {
            synchronized (mConfig) {
                try {
                    mConfig.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        final HttpURLConnection raw = adaptRequest(request);
        writeOutputStream(raw, request);
        return adaptResponse(raw, request);
    }

    @Override
    public void request(@NonNull BaseRequest request, @NonNull Callback callback) {
        // 异步请求要在子线程中执行，然后回调
        service().execute(() -> {
            try {
                final BaseResponse raw = request(request);
                mainHandler().post(() -> callback.onResponse(raw));
            } catch (IOException e) {
                mainHandler().post(() -> callback.onFailure(e));
            }
        });
    }

    @Override
    protected HttpURLConnection adaptRequest(BaseRequest request) throws IOException {
        return createConnection(request);
    }

    @Override
    protected BaseResponse adaptResponse(HttpURLConnection raw, BaseRequest current) throws IOException {
        return resolveConnection(raw, current);
    }

    private HttpURLConnection createConnection(BaseRequest request) throws IOException {
        final String url = buildUrl(request);
        final HttpURLConnection conn = openConnection(url, mConfig.proxy);
        setupClient(conn);
        conn.setRequestMethod(request.method);
        Map<String, String> headers = request.headers == null
                ? new HashMap<>()
                : request.headers;
        if (mConfig.userAgent != null) {
            headers.put("User-Agent", mConfig.userAgent);
        }
        for (String name : headers.keySet()) {
            conn.addRequestProperty(name, headers.get(name));
        }
        return conn;
    }

    private HttpURLConnection openConnection(String url, Proxy proxy) throws IOException {
        if (url.startsWith("https")) {
            // config https
        }
        if (proxy == null) {
            return (HttpURLConnection) new URL(url).openConnection();
        }
        return (HttpURLConnection) new URL(url).openConnection();
    }

    private void writeOutputStream(HttpURLConnection conn, BaseRequest request) throws IOException {
        conn.connect();
        if (!POST.equals(request.method) && !PUT.equals(request.method)) {
            return;
        }
        if (request.params == null || request.params.size() == 0) {
            return;
        }
        // 发送表单数据
        // final BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
    }

    private BaseResponse resolveConnection(HttpURLConnection conn, BaseRequest current)
            throws IOException {
        final BaseResponse response = new BaseResponse();
        // status line
        final String statusLine = conn.getHeaderField(null);
        if (statusLine != null) {
            final String[] elements = statusLine.split(" ");
            if (elements.length > 2) {
                response.protocol = elements[0];
                response.code = Integer.parseInt(elements[1]);
                response.msg = statusLine.substring(statusLine.indexOf(elements[1]) + 4);
            }
        } else {
            response.protocol = conn.getURL().getProtocol();
            response.code = conn.getResponseCode();
            response.msg = conn.getResponseMessage();
        }
        response.headers = new HashMap<>();
        final Map<String, List<String>> headerFields = conn.getHeaderFields();
        // skip status line
        headerFields.remove(null);
        for (String name : headerFields.keySet()) {
            response.headers.put(name, toString(headerFields.get(name)));
        }
        response.current = current;
        if (response.redirect()) {
            final String redirectUrl = response.headers.get("Location");
            if (redirectUrl != null) {
                final int old = redirectCount;
                redirectCount = old + 1;
                LogHelper.debug("DefHttpEngine", "redirect " + redirectUrl + " " + redirectCount);
                final BaseRequest redirect = new BaseRequest();
                redirect.method = current.method;
                redirect.url = redirectUrl;
                redirect.headers = current.headers;
                redirect.params = current.params;
                return request(redirect);
            }
        }
        if (response.clientError()) {
            if (response.code == 401 && mConfig.authenticate != null) {
                // 重新认证
                mReauthenticating = true;
                final BaseResponse raw = request(mConfig.authenticate);
                onReauthenticateResponse(raw);
                synchronized (mConfig) {
                    mReauthenticating = false;
                    mConfig.notifyAll();
                }
                // 继续发送`发生重定向时`的请求
                return request(response.current);
            }
        }
        response.data = toBytes(response.success() ? conn.getInputStream() : conn.getErrorStream());
        if (response.data == null || response.data.length == 0) {
            response.data = response.msg.getBytes();
        }
        trace("DefHttpEngine", response);
        return response;
    }

    private void onReauthenticateResponse(BaseResponse response) {
        LogHelper.debug("DefHttpEngine", "onReauthenticateResponse() " + response);
    }

    private byte[] toBytes(InputStream in) throws IOException {
        // do not close the `in`
        if (in == null) {
            return new byte[0];
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int len;
        byte[] data = new byte[8192];
        while ((len = in.read(data)) != -1) {
            buffer.write(data, 0, len);
        }
        buffer.flush();
        data = buffer.toByteArray();
        buffer.close();
        return data;
    }

    private static String toString(List<String> list) {
        if (list == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                builder.append("; ");
            }
            builder.append(list.get(i));
        }
        return builder.toString();
    }
}
