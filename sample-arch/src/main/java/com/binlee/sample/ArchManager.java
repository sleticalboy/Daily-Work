package com.binlee.sample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.binlee.sample.event.ConnectEvent;
import com.binlee.sample.event.DisconnectEvent;
import com.binlee.sample.event.IEvent;
import com.binlee.sample.event.ScanEvent;
import com.binlee.sample.util.Dispatcher;
import com.binlee.sample.util.EventHandler;
import com.binlee.sample.util.EventObserver;
import com.binlee.sample.util.InjectableHandler;
import com.binlee.sample.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final List<Record> mRecords = new CopyOnWriteArrayList<Record>() {
        @Override
        public boolean add(Record record) {
            return !contains(record) && super.add(record);
        }
    };

    private final Handler mWorker;
    private Context mContext;
    private EventHandler mEventHandler;
    private Dispatcher mDispatcher;
    private EventObserver mObserver;
    private BleScanner mScanner;

    public ArchManager() {
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mWorker = new InjectableHandler(thread.getLooper(), this);
    }

    @Override
    public void init(Context context) {
        mContext = context;

        initEventHandlers();

        mDispatcher = new Dispatcher();
        mObserver = new EventObserver(context, mWorker);
        mScanner = new BleScanner(context, mWorker);
    }

    @Override
    public Handler handler() {
        return mWorker;
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
        if (msg.what == IMessages.SCAN_RESULT) {
            onScanResult(((BluetoothDevice) msg.obj), msg.arg1);
            return true;
        } else if (msg.what == IMessages.SCAN_FAILED) {
            onScanFailed(((String) msg.obj), msg.arg1);
            return true;
        } else if (msg.what == IMessages.BONDED_CHANGED) {
            onBondedChanged(((BluetoothDevice) msg.obj), msg.arg1, msg.arg2);
            return true;
        } else if (msg.what == IMessages.GATT_CREATE_BOND) {
            onGattCreateBond(((BluetoothDevice) msg.obj));
            return true;
        } else if (msg.what == IMessages.HID_PROFILE_CHANGED) {
            onHidProfileChanged(((BluetoothDevice) msg.obj), msg.arg1);
            return true;
        } else if (msg.what == IMessages.LOCALE_CHANGED) {
            onLocaleChanged();
            return true;
        }
        return false;
    }

    private void onLocaleChanged() {
    }

    private void onHidProfileChanged(BluetoothDevice ble, int state) {
        if (state != BluetoothProfile.STATE_CONNECTED || ble == null) {
            return;
        }
        Record r = query(ble);
        String name = ble.getName();
        int type = ble.getType();
        if (type != BluetoothDevice.DEVICE_TYPE_LE) {
            return;
        }
        if (r == null) {
            // a new device
            r = new Record(ble);
            r.mCall = new ConnectEvent(ble, IEvent.REVERSED_CONNECT);
            mRecords.add(r);
        } else {
            if (r.mCall instanceof ConnectEvent) {
                ((ConnectEvent) r.mCall).connectGatt();
            }
        }
    }

    private Record query(BluetoothDevice ble) {
        for (final Record r : mRecords) {
            if (r.mDevice.equals(ble)) {
                return r;
            }
        }
        return null;
    }

    private void onGattCreateBond(BluetoothDevice ble) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            ble.createBond();
            return;
        }
        try {
            Method m = BluetoothDevice.class.getDeclaredMethod("createBond", int.class);
            m.setAccessible(true);
            m.invoke(ble, BluetoothDevice.TRANSPORT_LE);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void onBondedChanged(BluetoothDevice ble, int state, int reason) {
        if (state == BluetoothDevice.BOND_BONDED) {
            //
        } else if (state == BluetoothDevice.BOND_NONE) {
            // check reason
        }
    }

    private void onScanResult(BluetoothDevice ble, int arg1) {
        int type;
        if (arg1 == IEvent.ULTRA_SCAN) {
            type = IEvent.ULTRA_CONNECT;
        } else if (arg1 == IEvent.USB_SCAN) {
            type = IEvent.USB_CONNECT;
        } else if (arg1 == IEvent.REBOOT_SCAN) {
            type = IEvent.REBOOT_CONNECT;
        } else {
            return;
        }
        postEvent(new ConnectEvent(ble, type));
    }

    private void onScanFailed(String reason, int error) {
        logger().w(TAG, "onScanFailed() " + reason + ", " + error);
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
                    mScanner.start(((ScanEvent) event));
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
                    ConnectEvent call = (ConnectEvent) event;
                    call.setContext(mContext);
                    call.setHandler(mWorker);
                    if (mDispatcher.enqueue(call)) {
                        logger().w(TAG, "enqueue " + call + " success");
                    }
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
                case IEvent.CONFIG_DISCONNECT:
                case IEvent.OTHER_DISCONNECT:
                    mWorker.post(((DisconnectEvent) event));
                    return true;
            }
            return false;
        }
    }

    private static final class Record {

        private final BluetoothDevice mDevice;
        private AsyncCall mCall;

        public Record(BluetoothDevice device) {
            mDevice = device;
        }
    }
}
