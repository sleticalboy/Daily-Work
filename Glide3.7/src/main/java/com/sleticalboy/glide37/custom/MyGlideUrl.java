package com.sleticalboy.glide37.custom;

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
        return mUrl.replace(findToken(), "");
    }

    private String findToken() {
        String token = "";
        int tokenIndex = mUrl.contains("?token=")
                ? mUrl.indexOf("?token=") : mUrl.indexOf("&token=");
        if (tokenIndex != -1) {
            int nextAndIndex = mUrl.indexOf("&", tokenIndex + 1);
            if (nextAndIndex != -1) {
                token = mUrl.substring(tokenIndex + 1, nextAndIndex + 1);
            } else {
                token = mUrl.substring(tokenIndex);
            }
        }
        return token;
    }
}
