package com.binlee.sample.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.binlee.sample.util.Glog;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 21-2-5.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class InjectableHandler extends android.os.Handler {

    private static final String TAG = "WorkerHandler";
    private static final SparseArray<String> WHAT_ARRAY = new SparseArray<>();

    static {
        parseIWhat();
    }

    private static void parseIWhat() {
        int mod, what;
        for (final Field field : IWhat.class.getDeclaredFields()) {
            mod = field.getModifiers();
            if ((mod & (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL)) != 0) {
                try {
                    what = field.getInt(null);
                    WHAT_ARRAY.put(what, String.format("%s(0x%02x)", field.getName(), what));
                } catch (IllegalAccessException e) {
                    Glog.e(TAG, "parseIWhat() error.", e);
                }
            }
        }
        Glog.v(TAG, "parseIWhat() " + WHAT_ARRAY);
    }

    private final List<Callback> mCallbacks;
    private Callback mTracer;

    public InjectableHandler(@NonNull Looper looper) {
        this(looper, null);
    }

    public InjectableHandler(@NonNull Looper looper, @Nullable Callback callback) {
        super(looper, callback);
        mCallbacks = new CopyOnWriteArrayList<>();
        try {
            // 第一次注入 Callback 时通过反射注入 mCallback
            Field field = Handler.class.getDeclaredField("mCallback");
            field.setAccessible(true);
            field.set(this, new WrappedCallback(field.get(this)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String getMessageName(@NonNull Message msg) {
        return WHAT_ARRAY.get(msg.what, String.format("no name for(0x%02x)", msg.what));
    }

    public final void injectCallback(Callback callback) {

        if (callback instanceof TimeTracer) {
            mTracer = callback;
            return;
        }

        if (callback != null && !mCallbacks.contains(callback)) {
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

            if (mTracer != null && mTracer.handleMessage(msg)) {
                throw new RuntimeException("TimeTracer#handleMessage() must return false!");
            }

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
