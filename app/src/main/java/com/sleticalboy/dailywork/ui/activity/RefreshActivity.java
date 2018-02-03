package com.sleticalboy.dailywork.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.weight.PullRefreshView;

/**
 * Created on 18-2-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 测试下拉刷新自定义 View
 */
public class RefreshActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        PullRefreshView refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(RefreshActivity.this, "on refresh is doing", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
