package com.binlee.sample.util;

import com.binlee.sample.event.IEvent;

/**
 * Created on 21-2-5.
 *
 * @author binlee sleticalboy@gmail.com
 */
public abstract class EventHandler {

    private final EventHandler mNext;
    private OnUnhandledCallback mCallback;

    public EventHandler(EventHandler next) {
        mNext = next;
    }

    public final void setOnUnhandledCallback(OnUnhandledCallback callback) {
        mCallback = callback;
    }

    public final boolean handleEvent(IEvent event) {
        if (onProcess(event)) {
            return true;
        }
        if (mNext != null && mNext.handleEvent(event)) {
            return true;
        }
        if (mCallback != null) {
            mCallback.onUnhandledEvent(event);
        }
        return false;
    }

    protected abstract boolean onProcess(IEvent event);

    public interface OnUnhandledCallback {

        void onUnhandledEvent(IEvent event);
    }
}
