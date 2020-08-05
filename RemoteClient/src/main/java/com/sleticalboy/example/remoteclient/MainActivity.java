package com.sleticalboy.example.remoteclient;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String BASE_URI = "com.sleticalboy.dailywork.store";

    private TextView mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
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