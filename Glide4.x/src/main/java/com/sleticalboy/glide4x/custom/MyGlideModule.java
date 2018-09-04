package com.sleticalboy.glide4x.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.sleticalboy.glide4x.custom.okhttp.OkHttpGlideUrlLoader;
import com.sleticalboy.glide4x.custom.okhttp.ProgressInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created on 18-6-17.
 *
 * @author sleticalboy
 * @description
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {

    private static final String TAG = "MyGlideModule";

    private static final int DISK_CACHE_SIZE = DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE;
    private static final String DISK_CACHE_DIR = "sleticalboy_glide37";

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        Log.d(TAG, "applyOptions() called with: context = [" + context + "], builder = [" + builder + "]");
        // 自定义默认缓存目录以及默认缓存大小
//        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context,
//                DISK_CACHE_DIR, DISK_CACHE_SIZE));
        // 自定义默认图片加载质量
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        Log.d(TAG, "registerComponents() called with: context = [" + context + "], glide = [" + glide + "], registry = [" + registry + "]");
        // 替换 http 通讯组件为 okhttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        final OkHttpClient client = builder.build();
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory());
//        registry.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(client));
//        registry.append(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(client));
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(client));
//        registry.register(GlideUrl.class, InputStream.class,
//                new ResourceTranscoder<GlideUrl, InputStream>() {
//                    @Override
//                    public Resource<InputStream> transcode(Resource<GlideUrl> toTranscode,
//                                                           Options options) {
//                        final GlideUrl url = toTranscode.get();
//                        Request.Builder requestBuilder = new Request.Builder()
//                                .url(url.toStringUrl());
//                        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
//                            String key = headerEntry.getKey();
//                            requestBuilder.addHeader(key, headerEntry.getValue());
//                        }
//                        // 自定义 header
//                        requestBuilder.addHeader("httplib", "OkHttp");
//                        Request request = requestBuilder.build();
//                        Response response;
//                        try {
//                            response = client.newCall(request).execute();
//                            ResponseBody responseBody = response.body();
//                            return new SimpleResource<>(ContentLengthInputStream.obtain(
//                                    responseBody.byteStream(), responseBody.contentLength()));
//                        } catch (IOException ignored) {
//                            return null;
//                        }
//                    }
//                });
    }
}
