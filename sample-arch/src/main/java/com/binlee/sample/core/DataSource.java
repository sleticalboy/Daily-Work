package com.binlee.sample.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

import com.binlee.sample.event.AsyncEvent;
import com.binlee.sample.model.ArchDevice;
import com.binlee.sample.model.CacheEntry;
import com.binlee.sample.model.Database;
import com.binlee.sample.model.IDataSource;
import com.binlee.sample.util.Glog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 21-2-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DataSource implements IDataSource {

    private static final String TAG = Glog.wrapTag("DataSource");

    private Database mDatabase;
    private final List<Record> mRecords = new CopyOnWriteArrayList<Record>() {
        @Override
        public boolean add(Record record) {
            int index = indexOf(record);
            if (index < 0) return super.add(record);
            set(index, record);
            return true;
        }
    };

    private DataSource() {
        //no instance
    }

    private static final class Holder {
        static DataSource sModel = new DataSource();
    }

    public interface InitCallback {
        void onCompleted(boolean hasCache);
    }

    public static DataSource get() {
        return Holder.sModel;
    }

    public void init(Context context, InitCallback callback) {
        if (mDatabase != null) {
            Glog.w(TAG, "init() aborted as it has been initialized.");
            return;
        }
        mDatabase = new Database(context);

        if (callback != null) callback.onCompleted(getCaches().size() > 0);
    }

    public void put(Record record) {
        mRecords.add(record);
    }

    public Record getRecord(String mac) {
        for (final Record r : mRecords) {
            if (r.mDevice.target().getAddress().equals(mac)) return r;
        }
        return null;
    }

    public List<CacheEntry> getCaches() {
        return mDatabase.getCaches();
    }

    public void fetchCaches(Handler callback) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = adapter == null ? null : adapter.getBondedDevices();
        Glog.v(TAG, "fetchCaches() bonded devices: " + devices);
        List<ArchDevice> list = new ArrayList<>();
        if (devices == null || devices.size() == 0) {
            // fake data 0B:1F:09:C3:3F:BC
            list.add(new ArchDevice("0B:1F:09:C3:3F:BC"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:BD"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:BE"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:BF"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:C0"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:C1"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:C2"));
            list.add(new ArchDevice("0B:1F:09:C3:3F:C3"));
        } else {
            for (final BluetoothDevice ble : devices) {
                CacheEntry entry = getCache(ble.getAddress());
                if (entry == null) continue;
                list.add(new ArchDevice(ble));
            }
        }
        if (callback != null) callback.obtainMessage(IWhat.CACHE_FETCHED, list).sendToTarget();
    }

    public CacheEntry getCache(String address) {
        for (final CacheEntry entry : getCaches()) {
            if (address.equals(entry.mac)) return entry;
        }
        return null;
    }

    @Override
    public <T> T query(String key) {
        return mDatabase.query(key);
    }

    @Override
    public <T> List<T> queryAll(Class<T> clazz) {
        try {
            return mDatabase.queryAll(clazz);
        } catch (Throwable e) {
            Glog.e(TAG, "queryAll() error.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public <T> void update(T obj) {
        mDatabase.update(obj);
    }

    @Override
    public <T> void delete(T obj) {
        mDatabase.delete(obj);
    }

    public static final class Record {

        public final ArchDevice mDevice;
        public AsyncEvent mEvent;

        public Record(ArchDevice device) {
            mDevice = device;
        }
    }
}
