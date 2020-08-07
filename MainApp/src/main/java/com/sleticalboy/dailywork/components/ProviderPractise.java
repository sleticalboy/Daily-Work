package com.sleticalboy.dailywork.components;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

public class ProviderPractise extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URI = "com.sleticalboy.dailywork.store";

    private TextView mResult;

    @Override
    protected int layoutResId() {
        return R.layout.activity_provider;
    }

    @Override
    protected void initView() {
        mResult = findViewById(R.id.queryResult);
        final EditText table = findViewById(R.id.etTable);

        findViewById(R.id.btnQuery).setOnClickListener(view -> {
            doQuery(table.getText().toString().trim());
        });
    }

    private void doQuery(String table) {
        if (TextUtils.isEmpty(table)) {
            throw new IllegalArgumentException("table is null.");
        }
        final String[] projection = {"mac_address"};
        final Cursor cursor = getContentResolver().query(Uri.parse(BASE_URI + "/" + table),
                projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                final String mac = cursor.getString(cursor.getColumnIndex(projection[0]));
                mResult.setText(mac);
            }
            try {
                cursor.close();
            } catch (Throwable t) {
                Log.d(TAG, "doQuery() error with: " + table, t);
            }
        }
    }
}
