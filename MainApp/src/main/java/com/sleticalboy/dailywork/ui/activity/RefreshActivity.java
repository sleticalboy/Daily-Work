package com.sleticalboy.dailywork.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.weight.xrefresh.XRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-2-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 测试下拉刷新自定义 View
 */
public class RefreshActivity extends BaseActivity {

    private List<String> mDataList = new ArrayList<>();

    @Override
    protected void initData() {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.clear();
        for (int i = 0; i < 32; i++) {
            mDataList.add("Sub Item Data " + i);
        }
    }

    @Override
    protected void initView() {
        XRefreshLayout refreshLayout = findViewById(R.id.refresh_layout);

        refreshLayout.setOnLoadMoreListener(new XRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onPullUp() {
                //
            }

            @Override
            public void onPullDown() {
                //
            }
        });

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mDataList
        ));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("RefreshActivity", mDataList.get(position));
            }
        });
    }

    @Override
    protected int layoutResId() {
        return R.layout.activity_refresh;
    }
}
