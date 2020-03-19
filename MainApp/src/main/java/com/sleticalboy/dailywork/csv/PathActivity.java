package com.sleticalboy.dailywork.csv;

import android.view.View;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.weight.view.TestView;

/**
 * Created on 18-3-1.
 *
 * @author leebin
 * @version 1.0
 */
public class PathActivity extends BaseActivity {

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        TestView testView = findViewById(R.id.test);
        final View singleLine = findViewById(R.id.singleLine);
        // singleLine.setOnClickListener(v -> testView.postDelayed(() -> {
        //     testView.update();
        //     singleLine.performClick();
        // }, 250L));
        singleLine.setOnClickListener(v -> {
            testView.invalidate();
        });
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_path;
    }
}
