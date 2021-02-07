package com.binlee.sample.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * Created on 21-2-5.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class InjectableHandler extends android.os.Handler {

    private static Field sCallbackField;
    private Callback mCallback;

    public InjectableHandler(@NonNull Looper looper) {
        super(looper);
    }

    public InjectableHandler(@NonNull Looper looper, @Nullable Callback callback) {
        super(looper, callback);
    }

    public final void injectCallback(Callback callback) {
        mCallback = Preconditions.checkNotNull(callback, "");
        if (sCallbackField == null) {
            try {
                Field field = Handler.class.getDeclaredField("mCallback");
                field.setAccessible(true);
                sCallbackField = field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            sCallbackField.set(this, new WrappedCallback(sCallbackField.get(this)));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private final class WrappedCallback implements Callback {

        private final Callback mOriginal;

        public WrappedCallback(Object obj) {
            mOriginal = obj instanceof Callback ? ((Callback) obj) : null;
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (mCallback != null && mCallback.handleMessage(msg)) {
                return true;
            }
            return mOriginal != null && mOriginal.handleMessage(msg);
        }
    }
}
