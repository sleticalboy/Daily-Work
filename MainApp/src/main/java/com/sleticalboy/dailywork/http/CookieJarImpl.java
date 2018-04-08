package com.sleticalboy.dailywork.http;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created on 18-3-28.
 *
 * @author sleticalboy
 * @description Cookie 持久化「sp, db, file」
 */
public class CookieJarImpl implements CookieJar {

    private final SharedPreferences mPreferences;

    public CookieJarImpl(Context context) {
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        mPreferences = context.getSharedPreferences("cookie-jar", Context.MODE_PRIVATE);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        final Set<String> values = new HashSet<>();
        for (final Cookie cookie : cookies) {
            values.add(cookie.toString());
        }
        mPreferences.edit().putStringSet(url.host(), values).apply();
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        final Set<String> values = mPreferences.getStringSet(url.host(), new HashSet<>());
        List<Cookie> cookies = new ArrayList<>();
        for (final String value : values) {
            cookies.add(Cookie.parse(url, value));
        }
        return cookies;
    }
}
