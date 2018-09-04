package com.sleticalboy.okhttp25;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public final class CloseUtils {

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
