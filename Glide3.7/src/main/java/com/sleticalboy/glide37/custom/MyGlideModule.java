package com.sleticalboy.glide37.custom;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.sleticalboy.glide37.custom.okhttp.OkHttpGlideUrlLoader;
import com.sleticalboy.glide37.custom.okhttp.ProgressInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 */
public class MyGlideModule implements GlideModule {

    private static final int DISK_CACHE_SIZE = DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE;
    private static final String DISK_CACHE_DIR = "sleticalboy_glide37";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 自定义默认缓存目录以及默认缓存大小
       // builder.setDiskCache(new ExternalCacheDiskCacheFactory(context,
       //         DISK_CACHE_DIR, DISK_CACHE_SIZE));
        // 自定义默认图片加载质量
       // builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // 替换 http 通讯组件为 okhttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        final OkHttpClient client = builder.build();
        /*glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory());*/
        glide.register(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(client));
    }
}
