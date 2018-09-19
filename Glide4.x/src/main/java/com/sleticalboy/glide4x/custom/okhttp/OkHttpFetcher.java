package com.sleticalboy.glide4x.custom.okhttp;

import android.support.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 */
public class OkHttpFetcher implements DataFetcher<InputStream> {
    
    private final OkHttpClient client;
    private final GlideUrl url;
    private InputStream stream;
    private ResponseBody responseBody;
    private volatile boolean isCancelled;
    
    public OkHttpFetcher(OkHttpClient client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }
    
    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
        InputStream result;
        try {
            result = loadThisData(url.toStringUrl(), url.getHeaders());
        } catch (IOException e) {
            callback.onLoadFailed(e);
            return;
        }
        callback.onDataReady(result);
    }
    
    private InputStream loadThisData(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
            String key = headerEntry.getKey();
            builder.addHeader(key, headerEntry.getValue());
        }
        // TODO: 18-6-18 自定义 header
        builder.addHeader("httplib", "OkHttp");
        Request request = builder.build();
        if (isCancelled) {
            return null;
        }
        Response response = client.newCall(request).execute();
        responseBody = response.body();
        if (!response.isSuccessful() || responseBody == null) {
            throw new IOException("Request failed with code: " + response.code());
        }
        stream = ContentLengthInputStream.obtain(responseBody.byteStream(),
                responseBody.contentLength());
        return stream;
    }
    
    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
            if (responseBody != null) {
                responseBody.close();
            }
        } catch (IOException ignored) {
        }
    }
    
    @Override
    public void cancel() {
        isCancelled = true;
    }
    
    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }
    
    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
