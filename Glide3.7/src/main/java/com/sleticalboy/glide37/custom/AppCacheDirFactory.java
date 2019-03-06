package com.sleticalboy.glide37.custom;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 * @description
 */
public class AppCacheDirFactory extends DiskLruCacheFactory {

    public AppCacheDirFactory(String diskCacheFolder) {
        super(diskCacheFolder, 90);
    }

    public AppCacheDirFactory(String diskCacheFolder, String diskCacheName) {
        super(diskCacheFolder, diskCacheName, 90);
    }
}
