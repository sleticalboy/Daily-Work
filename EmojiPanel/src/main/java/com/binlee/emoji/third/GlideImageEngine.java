package com.binlee.emoji.third;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.binlee.emoji.compat.ImageEngine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class GlideImageEngine extends ImageEngine {

    private static final String TAG = "GlideImageEngine";

    @Override
    public void show(String url, ImageView target) {
        show(url, target, Config.asNull(), CALLBACK_NONE);
    }

    @Override
    public void showGif(String url, ImageView target) {
        show(url, target, Config.asGif(), CALLBACK_NONE);
    }

    @Override
    public void show(String url, ImageView target, Config config, Callback callback) {
        show(Drawable.class, url, target, config, callback);
    }

    @Override
    public <Res> void show(Class<Res> resClass, String url, ImageView target,
                           Config config, Callback callback) {
        final Config real = config == null ? Config.asNull() : config;
        Glide.with(target).as(resClass)
                .load(url)
                .apply(convertConfig(config))
                .listener(new RequestLogger<>())
                .into(new SimpleTarget<Res>() {
                    @Override
                    public void onResourceReady(@NonNull Res res,
                                                @Nullable Transition<? super Res> transition) {
                        if (res instanceof GifDrawable && real.mAsGif) {
                            target.setImageDrawable(((GifDrawable) res));
                            ((GifDrawable) res).start();
                        } else if (res instanceof Drawable) {
                            target.setImageDrawable(((Drawable) res));
                        } else if (res instanceof Bitmap) {
                            target.setImageBitmap(((Bitmap) res));
                        }
                    }
                });
    }

    @Override
    public void download(Context context, String url, Callback<File> callback) {
        if (callback == null) {
            throw new NullPointerException("download callback is null.");
        }
        final FutureTarget<File> target = Glide.with(context.getApplicationContext())
                .downloadOnly()
                .load(url)
                .listener(new RequestLogger<>())
                .submit();
        try {
            final File file = target.get();
            callback.onResReady(url, file);
        } catch (Throwable e) {
            callback.onFail(url, e);
            ANDROID.log(TAG, "#download error", e);
        }
    }

    @Override
    public void preload(Context context, PreloadCallback callback, String... urls) {
        if (urls == null || urls.length < 1) {
            throw new IllegalArgumentException("url is null or empty.");
        }
        final AtomicInteger counter = new AtomicInteger();
        final float unit = 100F / urls.length;
        final RequestManager mgr = Glide.with(context.getApplicationContext());
        final RequestLogger<Drawable> listener = new RequestLogger<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target, boolean isFirstResource) {
                onProgress(model, false);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model,
                                           Target<Drawable> target, DataSource dataSource,
                                           boolean isFirstResource) {
                onProgress(model, true);
                return false;
            }

            private void onProgress(Object model, boolean success) {
                if (callback == null) {
                    return;
                }
                final int count = counter.incrementAndGet();
                callback.onProgress(model, (int) (count * unit), success, count == urls.length);
            }
        };
        for (String url : urls) {
            mgr.load(url).listener(listener).preload();
        }
    }

    @Override
    protected RequestOptions convertConfig(Config config) {
        RequestOptions options = new RequestOptions();
        if (config == null) {
            return options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).dontTransform();
        }
        options = options.override(config.mWidth, config.mHeight);
        if (config.mCache == Cache.AUTOMATIC) {
            options = options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        } else if (config.mCache == Cache.RESOURCE) {
            options = options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        } else if (config.mCache == Cache.DISK) {
            options = options.skipMemoryCache(true);
        } else if (config.mCache == Cache.NONE) {
            options = options.diskCacheStrategy(DiskCacheStrategy.NONE);
        } else {
            // maybe null
            options = options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        }
        if (config.mStyle == Style.CENTER_CROP) {
            options = options.centerCrop();
        } else if (config.mStyle == Style.CENTER_INSIDE) {
            options = options.centerInside();
        } else if (config.mStyle == Style.FIT_CENTER) {
            options = options.fitCenter();
        } else if (config.mStyle == Style.CIRCLE) {
            options = options.circleCrop();
        } else if (config.mStyle == Style.ROUNDED) {
            options = options.transform(new RoundedCorners(config.mRadius));
        } else if (config.mStyle == Style.ROTATE) {
            options = options.transform(new Rotate(config.mDegree));
        } else {
            options = options.dontTransform();
        }
        return options;
    }

    private static class RequestLogger<R> implements RequestListener<R> {

        private static final String DEF_TAG = "RequestLogger";
        private final String mTag;

        RequestLogger() {
            this(DEF_TAG);
        }

        RequestLogger(@NonNull String tag) {
            mTag = tag;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, final Object model,
                                          Target<R> target, boolean isFirstResource) {
            ANDROID.log(mTag, "onLoadFailed() " + model, e);
            return false;
        }

        @Override
        public boolean onResourceReady(R resource, Object model, Target<R> target,
                                       DataSource dataSource, boolean isFirstResource) {
            ANDROID.log(mTag, "onResourceReady() " + model, null);
            return false;
        }
    }
}
