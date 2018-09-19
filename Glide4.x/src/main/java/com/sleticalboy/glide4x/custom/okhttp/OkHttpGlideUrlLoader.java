package com.sleticalboy.glide4x.custom.okhttp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 */
public class OkHttpGlideUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private static final Set<String> SCHEMES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("http", "https")));

    private OkHttpClient okHttpClient;

    public OkHttpGlideUrlLoader(OkHttpClient client) {
        this.okHttpClient = client;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull GlideUrl glideUrl, int width, int height, @NonNull Options options) {
        return new LoadData<>(glideUrl, new OkHttpFetcher(okHttpClient, glideUrl));
    }

    @Override
    public boolean handles(@NonNull GlideUrl glideUrl) {
        return SCHEMES.contains(Uri.parse(glideUrl.toStringUrl()).getScheme());
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {

        private final OkHttpClient client;

        public Factory(OkHttpClient client) {
            this.client = client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new OkHttpGlideUrlLoader(getOkHttpClient());
        }

        private OkHttpClient getOkHttpClient() {
            synchronized (client) {
                return client;
            }
        }

        @Override
        public void teardown() {
            // Do nothing
        }
    }
}
