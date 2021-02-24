package com.binlee.sample.core;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.binlee.sample.event.AsyncEvent;
import com.binlee.sample.util.Glog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 21-2-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DataSource {


    private static final String TAG = "DataSource";

    private Database mDatabase;

    private final List<CacheEntry> mEntries = new CopyOnWriteArrayList<CacheEntry>() {
        @Override
        public boolean add(CacheEntry entry) {
            int index = indexOf(entry);
            if (index < 0) return super.add(entry);
            set(index, entry);
            return true;
        }
    };

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
            Glog.w(TAG, "init() aborted as init done");
            return;
        }
        mDatabase = new Database(context);

        mEntries.clear();
        mEntries.addAll(getCaches());

        if (callback != null) callback.onCompleted(mEntries.size() > 0);
    }

    public void put(Record record) {
        mRecords.add(record);
    }

    public Record getRecord(BluetoothDevice ble) {
        for (final Record r : mRecords) {
            if (r.mDevice.target().equals(ble)) return r;
        }
        return null;
    }

    public List<CacheEntry> getCaches() {
        if (mEntries.size() != 0) return Collections.unmodifiableList(mEntries);
        return mDatabase.queryAll(CacheEntry.class);
    }

    public void fetchCaches(Handler callback) {
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (devices == null || devices.size() == 0) return;
        List<Device> list = new ArrayList<>();
        for (final BluetoothDevice ble : devices) {
            CacheEntry entry = getCache(ble.getAddress());
            if (entry == null) continue;
            list.add(new Device(ble));
        }
        if (callback != null) {
            callback.obtainMessage(IWhat.CACHE_FETCHED, list).sendToTarget();
        }
    }

    private CacheEntry getCache(String address) {
        for (final CacheEntry entry : mEntries) {
            if (address.equals(entry.mac)) return entry;
        }
        return null;
    }

    public static final class Record {

        public final Device mDevice;
        public AsyncEvent mCall;

        public Record(Device device) {
            mDevice = device;
        }
    }

    @Table(name = "cache_list")
    public static final class CacheEntry {

        @Column(name = "_mac", type = "TEXT", unique = true)
        public String mac;
        @Column(name = "_pipe", type = "INTEGER")
        public int pipe;
        @Column(name = "_channels", type = "TEXT")
        public String channels;
    }

    public static final class Device {

        private final BluetoothDevice mTarget;

        public Device(BluetoothDevice target) {
            mTarget = target;
        }

        public BluetoothDevice target() {
            return mTarget;
        }
    }

    private final static class Database extends SQLiteOpenHelper {

        private static final String TAG = "Database";

        private static final int DB_VERSION = 1;
        private static final String DB_NAME = "arch-settings.db";
        private static final Class<?>[] TABLE_CLASSES = {CacheEntry.class};
        private static final Map<Class<?>, TableInfo> TABLES = new ArrayMap<>();

        public Database(@Nullable Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            parseTables();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTables(db);
            createTables(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTables(db);
            createTables(db);
        }

        public <T> List<T> queryAll(Class<T> clazz) {
            TableInfo info = TABLES.get(clazz);
            if (info == null) throw new IllegalStateException("tables are null");
            try (Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + info.name, null)) {
                List<T> list = new ArrayList<>();
                while (c != null && c.moveToNext()) {
                    try {
                        T obj = cursorToObj(c, clazz);
                        if (obj == null) continue;
                        list.add(obj);
                    } catch (NoSuchMethodException | IllegalAccessException
                            | InvocationTargetException | InstantiationException e) {
                        Glog.e(TAG, "queryAll() error.", e);
                    }
                }
                return list;
            }
        }

        public <T> void update(T obj) {
            TableInfo info = TABLES.get(obj.getClass());
            if (info == null) return;
            try {
                long id = getWritableDatabase().insert(info.name, null, objToValues(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public <T> void delete(T obj) {
            TableInfo info = TABLES.get(obj.getClass());
            if (info == null) return;
            int id = getWritableDatabase().delete(info.name, "", new String[]{});
        }

        private static void parseTables() {
            for (final Class<?> clazz : TABLE_CLASSES) {
                TableInfo info = TABLES.get(clazz);
                if (info != null) continue;
                Table table = clazz.getDeclaredAnnotation(Table.class);
                if (table == null) throw new IllegalArgumentException("has no @Table annotation");
                final List<Pair<Field, Column>> columns = new ArrayList<>();
                for (final Field field : clazz.getDeclaredFields()) {
                    Column column = field.getDeclaredAnnotation(Column.class);
                    if (column == null) continue;
                    columns.add(Pair.create(field, column));
                }
                TABLES.put(clazz, new TableInfo(table.name(), columns));
            }
        }

        private static void createTables(SQLiteDatabase db) {
        }

        private static void dropTables(SQLiteDatabase db) {
        }

        private static ContentValues objToValues(Object obj) throws IllegalAccessException {
            if (obj == null) return null;
            TableInfo info = TABLES.get(obj.getClass());
            if (info == null) return null;
            ContentValues cv = new ContentValues(info.columns.size());
            for (final Pair<Field, Column> p : info.columns) {
                Object val = p.first.get(obj);
                String key = p.second.name();
                if (val instanceof Integer) {
                    cv.put(key, ((int) val));
                } else if (val instanceof Float) {
                    cv.put(key, ((float) val));
                } else if (val instanceof String) {
                    cv.put(key, ((String) val));
                } else if (val != null && val.getClass() == byte[].class) {
                    cv.put(key, ((byte[]) val));
                }
            }
            return cv;
        }

        private static <T> T cursorToObj(Cursor c, Class<T> clazz) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException, InstantiationException {
            TableInfo info = TABLES.get(clazz);
            if (info == null) return null;
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (final Pair<Field, Column> p : info.columns) {
                int index = c.getColumnIndex(p.second.name());
                int type = c.getType(index);
                Object val = null;
                if (type == Cursor.FIELD_TYPE_INTEGER) {
                    val = c.getInt(index);
                } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                    val = c.getFloat(index);
                } else if (type == Cursor.FIELD_TYPE_STRING) {
                    val = c.getString(index);
                } else if (type == Cursor.FIELD_TYPE_BLOB) {
                    val = c.getBlob(index);
                }
                if (val != null) p.first.set(obj, val);
            }
            return obj;
        }

        private final static class TableInfo {
            private final String name;
            private final List<Pair<Field, Column>> columns;

            public TableInfo(String name, List<Pair<Field, Column>> columns) {
                this.name = name;
                this.columns = columns;
            }
        }
    }

    @interface Table {
        String name();
    }

    @interface Column {
        String name();

        String type();

        String defVal() default "";

        boolean indexed() default false;

        boolean unique() default false;
    }
}
