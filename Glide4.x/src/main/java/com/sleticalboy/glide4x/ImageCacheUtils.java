package com.sleticalboy.glide4x;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.signature.EmptySignature;

import java.io.File;
import java.security.MessageDigest;

/**
 * Created on 18-10-13.
 *
 * @author leebin
 */
public final class ImageCacheUtils {

    public static long sCacheSize;
    public static String sCacheDir;

    private ImageCacheUtils() {
        //no instance
    }

    public static void clearImageCache(String uri) {
        final File cacheImage;
        if ((cacheImage = getCacheImage(uri)) != null) {
            if (!cacheImage.delete()) {
                final Throwable error = new SecurityException("Can not delete cache file");
                Log.e("ImageCacheUtils", error.getMessage(), error);
            }
        }
    }

    public static File getCacheImage(String uri) {
        if (uri == null || uri.trim().length() == 0) {
            return null;
        }
        return DiskLruCacheWrapper.create(getCacheDir(), getCacheSize()).get(CacheKey.create(uri));
    }

    private static File getCacheDir() {
        if (sCacheDir == null) {
            sCacheDir = DiskCache.Factory.DEFAULT_DISK_CACHE_DIR;
        }
        return new File(sCacheDir);
    }

    public static long getCacheSize() {
        if (sCacheSize == 0L) {
            sCacheSize = DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE;
        }
        return sCacheSize;
    }

    public static void cleanup(Context context) {
        clearDiskCache(context);
        clearMemoryCache(context);
    }

    public static void clearDiskCache(Context context) {
        final Context appContext = context.getApplicationContext();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            GlideExecutor.newDiskCacheExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Glide.get(appContext).clearDiskCache();
                }
            });
        } else {
            Glide.get(appContext).clearDiskCache();
        }
    }

    public static void clearMemoryCache(Context context) {
        final Context appContext = context.getApplicationContext();
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Glide.get(appContext).clearMemory();
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Glide.get(appContext).clearMemory();
                }
            });
        }
    }

    private static class CacheKey implements Key {

        final Key sourceKey;
        final Key signature;

        private CacheKey(Key sourceKey, Key signature) {
            this.sourceKey = sourceKey;
            this.signature = signature;
        }

        static CacheKey create(String uri) {
            return new CacheKey(new GlideUrl(uri), EmptySignature.obtain());
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            sourceKey.updateDiskCacheKey(messageDigest);
            signature.updateDiskCacheKey(messageDigest);
        }

        @Override
        public int hashCode() {
            int result = sourceKey.hashCode();
            result = 31 * result + signature.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey)) return false;

            final CacheKey cacheKey = (CacheKey) o;

            if (!sourceKey.equals(cacheKey.sourceKey)) return false;
            return signature.equals(cacheKey.signature);
        }
    }
}
