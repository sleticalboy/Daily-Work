package com.sleticalboy.dailywork.ui.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.weight.xrecycler.helper.MySnapHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class WheelRVActivity extends BaseActivity {

    private int[] mImagesId = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
    };

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        layoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(layoutManager);
        ItemAdapter adapter = new ItemAdapter(this, Arrays.asList(mImagesId));
        recyclerView.setAdapter(adapter);
        new LinearSnapHelper().attachToRecyclerView(recyclerView);
    }

    @Override
    protected int attachLayoutId() {
        return R.layout.activity_wheel_rv;
    }

    static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

        private Context mContext;
        private List<?> mData;

        public ItemAdapter(Context context, List<?> data) {
            mContext = context;
            mData = data;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        static class ItemHolder extends RecyclerView.ViewHolder {
            public ItemHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
