package com.binlee.sample;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Created on 21-2-5.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ArchManager implements IFunctions, Handler.Callback,
        EventHandler.OnUnhandledCallback {

    private static final String TAG = "ArchManager";
    private static final boolean DBG = true;

    private final Logger mLogger = new Logger() {
        @Override
        protected void log(String tag, String msg, int priority) {
            if (DBG) {
                Log.println(priority, tag, msg);
            }
        }
    };

    private Handler mHandler;
    private EventHandler mEventHandler;
    private Dispatcher mDispatcher;
    private EventObserver mObserver;

    @Override
    public void init(Context context) {

        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mHandler = new InjectableHandler(thread.getLooper(), this);

        initEventHandlers();

        mObserver = new EventObserver(context);
        mDispatcher = new Dispatcher();
    }

    @Override
    public Handler handler() {
        return mHandler;
    }

    @Override
    public void postEvent(IEvent event) {
        mEventHandler.handleEvent(event);
    }

    @Override
    public void onStart() {
        mObserver.onStart();
        mDispatcher.onStart();
    }

    @Override
    public void onDestroy() {
        mObserver.onDestroy();
        mDispatcher.onDestroy();
    }

    @Override
    public Logger logger() {
        return mLogger;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        return false;
    }

    @Override
    public void onUnhandledEvent(IEvent event) {
        logger().w(TAG, "onUnhandledEvent() " + event);
    }

    private void initEventHandlers() {
        EventHandler handler = new DisconnectEventHandler();
        handler.setOnUnhandledCallback(this);
        handler = new ConnectEventHandler(handler);
        handler.setOnUnhandledCallback(this);
        handler = new ScanEventHandler(handler);
        handler.setOnUnhandledCallback(this);
        mEventHandler = handler;
    }

    private final class ScanEventHandler extends EventHandler {

        private ScanEventHandler(EventHandler next) {
            super(next);
        }

        @Override
        public boolean onProcess(IEvent event) {
            switch (event.type()) {
                case IEvent.ULTRA_SCAN:
                case IEvent.REBOOT_SCAN:
                case IEvent.USB_SCAN:
                case IEvent.STOP_SCAN:
                    return true;
            }
            return false;
        }
    }

    private final class ConnectEventHandler extends EventHandler {

        private ConnectEventHandler(EventHandler next) {
            super(next);
        }

        @Override
        public boolean onProcess(IEvent event) {
            switch (event.type()) {
                case IEvent.ULTRA_CONNECT:
                case IEvent.REBOOT_CONNECT:
                case IEvent.USB_CONNECT:
                case IEvent.CLICK_CONNECT:
                case IEvent.CONFIG_CONNECT:
                    return true;
            }
            return false;
        }
    }

    private final class DisconnectEventHandler extends EventHandler {

        private DisconnectEventHandler() {
            super(null);
        }

        @Override
        public boolean onProcess(IEvent event) {
            switch (event.type()) {
                case IEvent.CLICK_DISCONNECT:
                case IEvent.UNBIND_DISCONNECT:
                case IEvent.OTHER_DISCONNECT:
                    return true;
            }
            return false;
        }
    }
}
