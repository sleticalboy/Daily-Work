package com.sleticalboy.imageloader;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Created on 18-4-28.
 *
 * @author sleticalboy
 * @description
 */
public final class ImageLoaderManager {

    public void display(String uri, ImageAware aware) {
        ImageLoader.getInstance().displayImage(uri, aware);
    }
}
