package com.binlee.emoji;

import com.binlee.emoji.third.GlideImageEngine;
import com.binlee.emoji.compat.ImageEngine;

public final class ImageAdapter {

    private static ImageEngine sEngine;

    private ImageAdapter() {
        throw new AssertionError("no instance.");
    }

    public static void setImageEngine(ImageEngine engine) {
        if (engine == null) {
            throw new NullPointerException("setImageEngine(): engine is null");
        }
        sEngine = engine;
    }

    public static ImageEngine engine() {
        if (sEngine == null) {
            sEngine = new GlideImageEngine();
        }
        return sEngine;
    }
}
