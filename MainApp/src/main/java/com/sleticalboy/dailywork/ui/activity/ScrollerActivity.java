package com.sleticalboy.dailywork.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

/**
 * Created on 18-6-10.
 *
 * @author sleticalboy
 * @description Scroller 使用练习
 */
public class ScrollerActivity extends BaseActivity {

    private LinearLayout layout;

    @Override
    protected int layoutResId() {
        return R.layout.activity_scroller;
    }

    @Override
    protected void initView() {
        layout = findViewById(R.id.layout);
        final Button scrollToBtn = findViewById(R.id.scroll_to_btn);
        final Button scrollByBtn = findViewById(R.id.scroll_by_btn);
        scrollToBtn.setOnClickListener(v -> layout.scrollTo(-60, -100));
        scrollByBtn.setOnClickListener(v -> layout.scrollBy(-60, -100));
    }

    @Override
    protected void initData() {
    }
}
