package com.sleticalboy.objectbox;

import android.content.Context;
import android.util.Log;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public final class BoxHelper {

    private BoxStore boxStore;
    private int initFlag = -1;

    private BoxHelper() {
    }

    public static BoxHelper getInstance() {
        return SingletonHolder.BOX_HELPER;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    public <T> Box<T> box(Context context, Class<T> cls) {
        checkInit(context);
        return boxStore.boxFor(cls);
    }

    private void checkInit(Context context) {
        if (boxStore == null) {
            boxStore = MyObjectBox.builder().androidContext(context).build();
        }
    }

    public void startService(Context context) {
        if (BuildConfig.DEBUG && initFlag < 0) {
            checkInit(context);
            boolean start = new AndroidObjectBrowser(boxStore).start(context);
            if (start) {
                initFlag = 1;
            }
            Log.d("ObjectBrowser", "start -> " + start);
        }
    }

    private static class SingletonHolder {
        static final BoxHelper BOX_HELPER = new BoxHelper();
    }
}
