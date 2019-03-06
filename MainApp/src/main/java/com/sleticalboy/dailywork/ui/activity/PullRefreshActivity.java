package com.sleticalboy.dailywork.ui.activity;

import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-2-4.
 *
 * @author leebin
 * @version 1.0
 * @description 测试第三方 Refresh 库
 */
public class PullRefreshActivity extends BaseActivity {

    private List<String> mDataList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    @Override
    protected int layoutResId() {
        return R.layout.activity_pull_refresh;
    }

    @Override
    protected void initView() {
        final PullToRefreshLayout refreshLayout = findViewById(R.id.pull_refresh);
        refreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                Log.d("PullRefreshActivity", "pull down to refresh");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData(true);
                        mAdapter.notifyDataSetChanged();
                        refreshLayout.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                Log.d("PullRefreshActivity", "pull up to refresh");
                new Handler().postDelayed(() -> {
                    loadData(false);
                    mAdapter.notifyDataSetChanged();
                    refreshLayout.finishLoadMore();
                }, 2000);
            }
        });

        ListView listView = findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, mDataList
        );
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> Log.d("RefreshActivity", mDataList.get(position)));
    }

    @Override
    protected void initData() {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        for (int i = 0; i < 20; i++) {
            mDataList.add("Sub Item Data " + i);
        }
    }

    private void loadData(boolean refresh) {
        for (int i = 0; i < 20; i++) {
            if (refresh) {
                mDataList.add(0, "Sub Item Data " + i);
            } else {
                mDataList.add("Sub Item Data " + i);
            }
        }
    }
}
