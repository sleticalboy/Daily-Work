package com.sleticalboy.dailywork;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created on 18-1-31.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class PermissionCheckActivity extends AppCompatActivity {

    protected String[] permissions = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
    };
    protected static final int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != 0
                || ContextCompat.checkSelfPermission(this, permissions[1]) != 0) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionCheckActivity", permissions[i] + " 没有授权");
                }
            }
        }
    }
}
