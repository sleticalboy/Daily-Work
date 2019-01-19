package com.sleticalboy.glide4x.custom;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.sleticalboy.glide4x.custom.okhttp.DefaultEventListener;
import com.sleticalboy.glide4x.custom.okhttp.HttpLoggerInterceptor;
import com.sleticalboy.glide4x.custom.okhttp.OkHttpGlideUrlLoader;
import com.sleticalboy.glide4x.custom.okhttp.ProgressInterceptor;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 */
@GlideModule
public class MyGlideModule extends AppGlideModule {
    
    private static final String TAG = "MyGlideModule";
    
    private static final int DISK_CACHE_SIZE = DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE;
    private static final String DISK_CACHE_DIR = "sleticalboy_glide37";
    
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        Log.d(TAG, "applyOptions() called with: context = [" + context + "], builder = [" + builder + "]");
        // 自定义默认图片加载质量
        // builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        // 自定义默认缓存目录以及默认缓存大小
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, "", 1024L));
    }
    
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        Log.d(TAG, "registerComponents() called with: context = [" + context + "], glide = [" + glide + "], registry = [" + registry + "]");
        // 替换 http 通讯组件为 okhttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        builder.addInterceptor(new HttpLoggerInterceptor().setLevel(HttpLoggerInterceptor.Level.BODY));
        builder.eventListener(new DefaultEventListener());
        final OkHttpClient client = builder.build();
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory(client));
    }
}
