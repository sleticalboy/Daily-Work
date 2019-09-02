package com.binlee.emoji.helper;

import android.text.TextUtils;

import java.io.File;

public class UrlHelper {

    private UrlHelper() {
        throw new AssertionError("no instance.");
    }

    public static String inspectUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        if (isFile(url) || isHttp(url) || isAndroid(url)) {
            return url;
        }
        if (!isHttp(url) && url.startsWith("/")) {
            url = baseUrl() + url;
        } else if (!isHttp(url) && !url.startsWith("/")) {
            url = baseUrl() + File.separator + url;
        }
        return url;
    }

    public static boolean isFile(String url) {
        return url != null && url.startsWith("file://");
    }

    public static boolean isHttp(String url) {
        return url != null && url.startsWith("http");
    }

    public static boolean isAndroid(String url) {
        return url != null && url.startsWith("android.");
    }

    public static String baseUrl() {
        return "";
    }
}
