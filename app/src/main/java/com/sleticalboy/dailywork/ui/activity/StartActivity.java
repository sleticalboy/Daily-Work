package com.sleticalboy.dailywork.ui.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created on 18-1-29.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 启动界面
 */
public class StartActivity extends ListActivity {

    private final ItemHolder[] dataList = {
            new ItemHolder(StockActivity.class, "股票 View"),
            new ItemHolder(SmsSenderActivity.class, "加密短信发送"),
            new ItemHolder(RefreshActivity.class, "下拉刷新 View"),
            new ItemHolder(PullRefreshActivity.class, "下拉刷新库测试"),
            new ItemHolder(WheelRVActivity.class, "RecyclerView 轮播"),
            new ItemHolder(DecorationActivity.class, "RecyclerView 添加 item 分割线 / 拖拽排序"),
            new ItemHolder(PagerActivity.class, "RecyclerView 分页"),
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListAdapter listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, dataList);
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ItemHolder holder = dataList[position];
        ((TextView) v).setText(holder.mClassName);
        startActivity(new Intent(this, holder.mClass));
    }

    private static class ItemHolder {
        Class<?> mClass;
        String mClassName;

        public ItemHolder(Class<?> aClass, String className) {
            mClass = aClass;
            mClassName = className;
        }

        @Override
        public String toString() {
            return mClassName;
        }
    }
}
