package com.sleticalboy.okhttp25;

import androidx.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class OkUtils {
    
    public static ThreadFactory threadFactory(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                Thread result = new Thread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }
    
    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
