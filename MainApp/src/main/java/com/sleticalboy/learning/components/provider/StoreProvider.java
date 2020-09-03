package com.sleticalboy.learning.components.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

public class StoreProvider extends ContentProvider {

    private static final String TAG = "StoreProvider";

    private static final String AUTHORITY = "com.sleticalboy.dailywork.store";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, "/cache", 0);
        URI_MATCHER.addURI(AUTHORITY, "/pair", 1);
    }

    public StoreProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // 此方法会比 Application 的 onCreate() 方法先执行
        Log.d(TAG, "onCreate() start --->");
        StoreManager.init(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final MatrixCursor cursor = new MatrixCursor(projection);
        int value = 0;
        final String table = uri.getLastPathSegment();
        String sql = "select mac from device_list where mac=? and status";
        if ("cache".equals(table)) {
            sql += "=0";
        } else if ("pair".equals(table)) {
            sql += ">=3";
        } else {
            cursor.addRow(new Object[]{value});
            return cursor;
        }
        final Cursor c = StoreManager.get().readableDb().rawQuery(sql, projection);
        if (c != null) {
            value = c.getCount() != 0 ? 1 : 0;
            c.close();
        }
        cursor.addRow(new Object[]{value});
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}