package com.binlee.emoji;

import com.binlee.emoji.compat.HttpEngine;
import com.binlee.emoji.third.OkHttpEngine;

public class HttpAdapter {

    private static HttpEngine sEngine;

    private HttpAdapter() {
        throw new AssertionError("no instance.");
    }

    public static void setHttpEngine(HttpEngine engine) {
        if (engine == null) {
            throw new NullPointerException("engine is null");
        }
        sEngine = engine;
    }

    public static HttpEngine engine() {
        if (sEngine == null) {
            sEngine = new OkHttpEngine();
            // sEngine = new DefaultHttpEngine();
            // sEngine = new SocketHttpEngine();
        }
        return sEngine;
    }
}
