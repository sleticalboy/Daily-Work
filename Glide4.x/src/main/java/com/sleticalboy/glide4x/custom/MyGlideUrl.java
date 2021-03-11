package com.sleticalboy.glide4x.custom;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * <p>
 * from <a href='https://blog.csdn.net/guolin_blog/article/details/54895665'>guolin</a>
 *
 * @author guolin
 */
public class MyGlideUrl extends GlideUrl {

    private final String mUrl;

    public MyGlideUrl(String url) {
        super(url);
        mUrl = url;
    }

    @Override
    public String getCacheKey() {
        return eraseToken(mUrl);
    }

    private static String eraseToken(String url) {
        int index = url.contains("?token=") ? url.indexOf("?token=") : url.indexOf("&token=");
        if (index < 0) return url;
        String token;
        int nextAndIndex = url.indexOf("&", index + 1);
        if (nextAndIndex != -1) {
            token = url.substring(index + 1, nextAndIndex + 1);
        } else {
            token = url.substring(index);
        }
        return url.replace(token, "");
    }
}
