package com.binlee.sample.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import com.binlee.sample.event.AsyncEvent;
import com.binlee.sample.event.ConnectEvent;
import com.binlee.sample.event.IEvent;
import com.binlee.sample.event.ScanEvent;
import com.binlee.sample.util.ConfigAssigner;
import com.binlee.sample.util.Glog;
import com.binlee.sample.view.IView;
import com.binlee.sample.view.ViewProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created on 21-2-5.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ArchManager implements IArchManager, Handler.Callback,
        EventDispatcher.OnUnhandledCallback {

    private static final String TAG = "ArchManager";

    private final ViewProxy mViewProxy;
    private final Handler mWorker;
    private final DataSource mSource;
    private Context mContext;
    private EventDispatcher mEventDispatcher;
    private EventExecutor mEventExecutor;
    private EventObserver mObserver;
    private BleScanner mScanner;
    private ConfigAssigner mAssigner;

    public ArchManager() {
        mSource = DataSource.get();
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mWorker = new InjectableHandler(thread.getLooper(), this);
        mViewProxy = new ViewProxy();
    }

    @Override
    public void onCreate(Context context) {
        mContext = context;

        initDispatchers();

        mEventExecutor = new EventExecutor();
        mObserver = new EventObserver(context, mWorker);
        mScanner = new BleScanner(context, mWorker);
    }

    @Override
    public Handler handler() {
        return mWorker;
    }

    @Override
    public void postEvent(IEvent event) {
        mEventDispatcher.deliver(event);
    }

    @Override
    public void onStart() {
        mObserver.onStart();
        mEventExecutor.onStart();
    }

    @Override
    public void onDestroy() {
        mObserver.onDestroy();
        mEventExecutor.onDestroy();
    }

    @Override
    public void attachView(IView view) {
        mViewProxy.setTarget(view);

        // in worker handler
        mSource.fetchCaches(mWorker);
    }

    @Override
    public void detachView() {
        mViewProxy.setTarget(null);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case IWhat.SCAN_RESULT:
                onScanResult(((BluetoothDevice) msg.obj), msg.arg1);
                return true;
            case IWhat.SCAN_FAILED:
                onScanFailed(((String) msg.obj), msg.arg1);
                return true;
            case IWhat.BONDED_CHANGED:
                onBondedChanged(((BluetoothDevice) msg.obj), msg.arg1, msg.arg2);
                return true;
            case IWhat.GATT_CREATE_BOND:
                onGattCreateBond(((BluetoothDevice) msg.obj));
                return true;
            case IWhat.HID_PROFILE_CHANGED:
                onHidProfileChanged(((BluetoothDevice) msg.obj), msg.arg1);
                return true;
            case IWhat.LOCALE_CHANGED:
                onLocaleChanged();
                return true;
            case IWhat.GATT_START_CONFIG:
                onGattStartConfig(((BluetoothDevice) msg.obj));
                return true;
            case IWhat.CONNECT_STATUS_CHANGE:
                onConnectStatusChanged(((ConnectEvent) msg.obj), msg.arg1);
                return true;
        }
        return false;
    }

    private void onConnectStatusChanged(ConnectEvent event, int status) {
        if (status == AsyncEvent.STATUS_CONFIG_OVER) {
            //
        }
    }

    private void onGattStartConfig(BluetoothDevice ble) {
        DataSource.Record r = mSource.getRecord(ble);
        if (r != null && r.mCall instanceof ConnectEvent) {
            if (mAssigner == null) {
                mAssigner = new ConfigAssigner();
            }
            if (mAssigner.assign(r.mDevice)) {
                if (((ConnectEvent) r.mCall).startConfig(0, "a00248")) {
                    Glog.v(TAG, "onGattStartConfig() ...");
                }
            }
        }
    }

    private void onLocaleChanged() {
        // 更新 dialog 等
    }

    private void onHidProfileChanged(BluetoothDevice ble, int state) {
        if (state != BluetoothProfile.STATE_CONNECTED || ble == null) {
            return;
        }
        DataSource.Record r = mSource.getRecord(ble);
        String name = ble.getName();
        int type = ble.getType();
        if (type != BluetoothDevice.DEVICE_TYPE_LE) {
            return;
        }
        if (r == null) {
            // a new device
            mSource.put(r = new DataSource.Record(new DataSource.Device(ble)));
            r.mCall = new ConnectEvent(ble, IEvent.REVERSED_CONNECT);
        } else {
            if (r.mCall instanceof ConnectEvent) {
                ((ConnectEvent) r.mCall).connectGatt();
            }
        }
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
        Glog.w(TAG, "onScanFailed() " + reason + ", " + error);
    }

    @Override
    public void onUnhandled(IEvent event) {
        Glog.w(TAG, "onUnhandledEvent() " + event);
    }

    private void initDispatchers() {

        EventDispatcher handler = new AsyncEventDispatcher();
        handler.setOnUnhandledCallback(this);

        handler = new ScanEventDispatcher(handler);
        handler.setOnUnhandledCallback(this);
        mEventDispatcher = handler;
    }

    // 处理扫描事件
    private final class ScanEventDispatcher extends EventDispatcher {

        private ScanEventDispatcher(EventDispatcher next) {
            super(next);
        }

        @Override
        public boolean onProcess(IEvent event) {
            if (!(event instanceof ScanEvent)) return false;
            mScanner.start(((ScanEvent) event));
            return true;
        }
    }

    // 处理连接 && 断开事件
    private final class AsyncEventDispatcher extends EventDispatcher {

        public AsyncEventDispatcher() {
            super(null);
        }

        @Override
        protected boolean onProcess(IEvent event) {
            if (!(event instanceof AsyncEvent)) return false;

            execEvent(((AsyncEvent) event));

            return true;
        }

        private void execEvent(AsyncEvent event) {
            event.setContext(mContext);
            event.setHandler(mWorker);
            if (mEventExecutor.submit(event) && event instanceof ConnectEvent) {
                // start fast loop read
            }
        }
    }
}
