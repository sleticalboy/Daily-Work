package com.binlee.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.binlee.sample.util.Glog;
import com.binlee.sample.view.DeviceListFragment;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = Glog.wrapTag("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // show Fragment
        showFragment();
    }

    private void showFragment() {
        Glog.v(TAG, "showFragment() ");
        FragmentManager mgr = getSupportFragmentManager();
        Fragment fragment = mgr.findFragmentByTag(DeviceListFragment.TAG);
        FragmentTransaction transaction = mgr.beginTransaction();
        if (fragment == null) {
            fragment = DeviceListFragment.newInstance(1);
            transaction.add(R.id.fl_container, fragment, DeviceListFragment.TAG);
        }
        transaction.show(fragment).commitNow();
    }
}