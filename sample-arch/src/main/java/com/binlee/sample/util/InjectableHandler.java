package com.binlee.sample.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 21-2-5.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class InjectableHandler extends android.os.Handler {

    private List<Callback> mCallbacks;

    public InjectableHandler(@NonNull Looper looper) {
        this(looper, null);
    }

    public InjectableHandler(@NonNull Looper looper, @Nullable Callback callback) {
        super(looper, callback);
        mCallbacks = new CopyOnWriteArrayList<>();
    }

    public final void injectCallback(Callback callback) {
        Preconditions.checkNotNull(callback, "callback must not be null");
        if (mCallbacks == null) {
            // 第一次注入 Callback 时通过反射注入 mCallback
            mCallbacks = new CopyOnWriteArrayList<>();
            try {
                Field field = Handler.class.getDeclaredField("mCallback");
                field.setAccessible(true);
                field.set(this, new WrappedCallback(field.get(this)));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    private final class WrappedCallback implements Callback {

        private final Callback mOriginal;

        public WrappedCallback(Object obj) {
            mOriginal = obj instanceof Callback ? ((Callback) obj) : null;
        }

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            // 原有的 Callback 处理消息的优先级最高
            if (mOriginal != null && mOriginal.handleMessage(msg)) {
                return true;
            }
            // 倒序遍历，后添加的 Callback 优先处理消息
            for (int i = mCallbacks.size() - 1; i >= 0; i--) {
                if (mCallbacks.get(i).handleMessage(msg)) {
                    return true;
                }
            }
            return false;
        }
    }
}
