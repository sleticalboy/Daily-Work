package com.sleticalboy.dailywork.ui.activity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

public class IntentCameraUI extends BaseActivity {

    @Override
    protected int layoutResId() {
        return R.layout.activity_intent_camera;
    }

    @Override
    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    protected void initData() {

    }

}
