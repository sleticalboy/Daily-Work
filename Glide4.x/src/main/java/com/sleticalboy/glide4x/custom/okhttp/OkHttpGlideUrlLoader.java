package com.sleticalboy.glide4x.custom.okhttp;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.sleticalboy.glide4x.custom.MyGlideUrl;

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
public class OkHttpGlideUrlLoader implements ModelLoader<MyGlideUrl, InputStream> {

    private static final Set<String> SCHEMES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("http", "https")));

    private final OkHttpClient okHttpClient;

    public OkHttpGlideUrlLoader(OkHttpClient client) {
        this.okHttpClient = client;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull MyGlideUrl glideUrl, int width, int height,
                                               @NonNull Options options) {
        return new LoadData<>(glideUrl, new OkHttpFetcher(okHttpClient, glideUrl));
    }

    @Override
    public boolean handles(@NonNull MyGlideUrl glideUrl) {
        return SCHEMES.contains(Uri.parse(glideUrl.toStringUrl()).getScheme());
    }

    public static class Factory implements ModelLoaderFactory<MyGlideUrl, InputStream> {

        private final OkHttpClient client;

        public Factory(OkHttpClient client) {
            this.client = client;
        }

        @NonNull
        @Override
        public ModelLoader<MyGlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
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
