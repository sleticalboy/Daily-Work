package com.sleticalboy.sample.client;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.widget.Toast;

import com.sleticalboy.sample.service.DataStruct;
import com.sleticalboy.sample.service.ITest;

/**
 * Created on 21-1-5.
 *
 * @author binli
 */
public class SampleClient extends Activity {

    private static final String TAG = "SampleClient";

    private ITest mService;
    private final ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected() name: " + name + ", binder: " + binder);
            mService = ITest.Stub.asInterface(binder);
            SampleClient.this.onServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void onServiceConnected() {
        if (mService != null) {
            Toast.makeText(this, R.string.service_bind_ok, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sample_client);

        doBindeService();

        initViews();
    }

    private void initViews() {
        findViewById(R.id.btn_write).setOnClickListener(v -> {
            if (mService == null) {
                return;
            }
            try {
                DataStruct data = new DataStruct();
                data.mName = "data to server";
                data.mNotify = true;
                mService.doWrite(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.btn_read).setOnClickListener(v -> {
            if (mService == null) {
                return;
            }
            try {
                mService.doRead("client", false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.btn_native).setOnClickListener(v -> {
            // 只要有权限，是能查询到 ServiceManager 中注册的 service
            IBinder service = ServiceManager.getService("sample.service");
            Log.w(TAG, "sample.service: " + service);
        });
    }

    private void doBindeService() {
        // com.sleticalboy.sample.service.SampleService
        Intent intent = new Intent()
                .setClassName("com.sleticalboy.sample.service",
                        "com.sleticalboy.sample.service.SampleService");
        boolean bound = bindService(intent, mConn, BIND_AUTO_CREATE);
        Log.d(TAG, "bind service: " + bound);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
    }
}
