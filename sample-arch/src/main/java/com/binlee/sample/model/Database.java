package com.binlee.sample.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.binlee.sample.util.Glog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 21-2-26.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class Database extends SQLiteOpenHelper {

    private static final String TAG = Glog.wrapTag("Database");

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "arch-settings.db";
    private static final Class<?>[] TABLE_CLASSES = {CacheEntry.class};
    private static final Map<Class<?>, TableInfo> TABLES = new ArrayMap<>();

    static {
        parseTables();
    }

    private final List<CacheEntry> mEntries = new CopyOnWriteArrayList<CacheEntry>() {
        @Override
        public boolean add(CacheEntry entry) {
            int index = indexOf(entry);
            if (index < 0) return super.add(entry);
            set(index, entry);
            return true;
        }
    };

    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        init();
    }

    private void init() {
        mEntries.clear();
        try {
            mEntries.addAll(getCaches());
        } catch (Exception e) {
            Glog.e(TAG, "init() error.", e);
        }
    }

    public List<CacheEntry> getCaches() {
        if (mEntries.size() != 0) return mEntries;
        return queryAll(CacheEntry.class);
    }

    public <T> T query(String key) {
        return null;
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
            Glog.w(TAG, "update() id: " + id);
        } catch (IllegalAccessException e) {
            Glog.e(TAG, "update() error.", e);
        }
    }

    public <T> void delete(T obj) {
        TableInfo info = TABLES.get(obj.getClass());
        if (info == null) return;
        try {
            int id = getWritableDatabase().delete(info.name, "", new String[]{});
            Glog.w(TAG, "delete() id: " + id);
        } catch (Exception e) {
            Glog.e(TAG, "delete() error.", e);
        }
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

    private static void parseTables() {
        for (final Class<?> clazz : TABLE_CLASSES) {
            TableInfo info = TABLES.get(clazz);
            if (info != null) continue;
            Table table = getTable(clazz);
            if (table == null) {
                Glog.w(TAG, clazz.getName() + " has no @Table annotation.");
                continue;
            }
            final List<Pair<Field, Table.Column>> columns = new ArrayList<>();
            for (final Field field : clazz.getDeclaredFields()) {
                Table.Column column = getColumn(field);
                if (column == null) {
                    Glog.w(TAG, field.getName() + " has no @Table.Column annotation.");
                    continue;
                }
                columns.add(Pair.create(field, column));
            }
            TABLES.put(clazz, new TableInfo(table.name(), columns));
        }
        Glog.d(TAG, "parseTables() " + TABLES);
    }

    private static Table.Column getColumn(Field field) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? field.getDeclaredAnnotation(Table.Column.class)
                : field.getAnnotation(Table.Column.class);
    }

    private static Table getTable(Class<?> clazz) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? clazz.getDeclaredAnnotation(Table.class)
                : clazz.getAnnotation(Table.class);
    }

    private static void createTables(SQLiteDatabase db) {
        db.execSQL("create table if not exists cache_list(" +
                "_id integer primary key autoincrement, " +
                "_mac text, _pipe integer, _channels text" +
                ");");
    }

    private static void dropTables(SQLiteDatabase db) {
    }

    private static ContentValues objToValues(Object obj) throws IllegalAccessException {
        if (obj == null) return null;
        TableInfo info = TABLES.get(obj.getClass());
        if (info == null) return null;
        ContentValues cv = new ContentValues(info.columns.size());
        for (final Pair<Field, Table.Column> p : info.columns) {
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
        for (final Pair<Field, Table.Column> p : info.columns) {
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
        private final List<Pair<Field, Table.Column>> columns;

        public TableInfo(String name, List<Pair<Field, Table.Column>> columns) {
            this.name = name;
            this.columns = columns;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("TableInfo{").append("name='").append(name).append('\'');
            buffer.append(", columns=");
            if (columns == null || columns.size() == 0) {
                buffer.append("{}");
            } else {
                for (final Pair<Field, Table.Column> p : columns) {
                    buffer.append(p.first.getName()).append(":").append(p.second);
                }
            }
            buffer.append('}');
            return buffer.toString();
        }
    }
}
