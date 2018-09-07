package com.sleticalboy.okhttp25;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sleticalboy.okhttp25.ui.HttpRequestActivity;

public final class MainListActivity extends ListActivity {

    private ItemHolder[] mItemData = {
            new ItemHolder(HttpRequestActivity.class, "http request")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mItemData));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ItemHolder itemHolder = mItemData[position];
        ((TextView) v).setText(itemHolder.mTitle);
        startActivity(new Intent(this, itemHolder.mActivityClass));
    }

    static class ItemHolder {

        final Class<? extends Activity> mActivityClass;
        final String mTitle;

        ItemHolder(Class<? extends Activity> activityClass, String title) {
            mActivityClass = activityClass;
            mTitle = title;
        }

        @Override
        public String toString() {
            return mTitle;
        }
    }

}
