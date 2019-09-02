package com.binlee.emoji.compat;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.IntDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class ImageEngine {

    protected static final Logger ANDROID = (tag, msg, e) -> {
        final int priority = e == null ? Log.DEBUG : Log.ERROR;
        if (e != null) {
            msg += '\n' + Log.getStackTraceString(e);
        }
        Log.println(priority, tag, msg);
    };

    protected static final Callback CALLBACK_NONE = new Callback() {
        @Override
        public void onResReady(Object model, Object res) {
        }

        @Override
        public void onFail(Object model, Throwable e) {
        }
    };

    public abstract void show(String url, ImageView target);

    public abstract void showGif(String url, ImageView target);

    public abstract void show(String url, ImageView target, Config config, Callback callback);

    public abstract <Res> void show(Class<Res> resClass, String url, ImageView target,
                                    Config config, Callback callback);

    public abstract void download(Context context, String url, Callback<File> callback);

    public abstract void preload(Context context, PreloadCallback callback, String... urls);

    protected abstract Object convertConfig(Config config);

    public static final class Config implements Cloneable {
        // -1 表示原始尺寸
        public int mWidth = -1;
        public int mHeight = -1;
        @Cache
        public int mCache = Cache.NONE;
        @Style
        public int mStyle = Style.FIT_CENTER;
        public int mRadius;
        public int mDegree;
        public boolean mAsGif = false;

        public static Config asGif() {
            final Config config = new Config();
            config.mAsGif = true;
            return config;
        }

        public static Config asNull() {
            return new Config();
        }

        public static Config apply(Config that) {
            return that == null ? asNull() : copy(that);
        }

        @Override
        public Config clone() {
            return copy(this);
        }

        private static Config copy(Config source) {
            if (source == null) {
                return asNull();
            }
            final Config copy = new Config();
            copy.mWidth = source.mWidth;
            copy.mHeight = source.mHeight;
            copy.mStyle = source.mStyle;
            copy.mRadius = source.mRadius;
            copy.mDegree = source.mDegree;
            copy.mAsGif = source.mAsGif;
            return copy;
        }
    }

    public interface Callback<Res> {

        void onResReady(Object model, Res res);

        void onFail(Object model, Throwable e);
    }

    /**
     * 预加载资源回调
     */
    public interface PreloadCallback {

        /**
         * 预加载进度展示
         *
         * @param model     预加载的资源路径
         * @param progress  全部预加载资源进度
         * @param success   加载的单个资源是否成功
         * @param completed 全部资源是否预加载完毕
         */
        void onProgress(Object model, int progress, boolean success, boolean completed);
    }

    public interface Logger {
        void log(String tag, String msg, Throwable e);
    }

    @IntDef({Cache.NONE, Cache.DISK, Cache.RESOURCE, Cache.AUTOMATIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Cache {
        int NONE = 0x00;
        int DISK = 0x01;
        int RESOURCE = 0x03;
        int AUTOMATIC = 0x04;
    }

    @IntDef({
            Style.FIT_CENTER, Style.CENTER_CROP, Style.CENTER_INSIDE,
            Style.CIRCLE, Style.ROUNDED, Style.ROTATE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Style {
        int FIT_CENTER = 0x10;
        int CENTER_CROP = 0x11;
        int CENTER_INSIDE = 0x12;
        int CIRCLE = 0x13;
        int ROUNDED = 0x14;
        int ROTATE = 0x15;
    }
}
