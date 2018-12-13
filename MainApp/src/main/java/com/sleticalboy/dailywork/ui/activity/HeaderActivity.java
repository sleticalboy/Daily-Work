package com.sleticalboy.dailywork.ui.activity;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created on 18-6-6.
 *
 * @author sleticalboy
 * @description
 */
public class HeaderActivity extends BaseActivity {

    private static final List<String> M_DATA_SET = new ArrayList<>();

    @Override
    protected int layoutResId() {
        return R.layout.activity_header;
    }

    @Override
    protected void initView() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d("HeaderActivity", "verticalOffset:" + verticalOffset);
            }
        });


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(new RecyclerAdapter());
    }

    @Override
    protected void initData() {
        for (int i = 0; i < 500; i++) {
            M_DATA_SET.add(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)));
        }
    }

    static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tvRecyclerItem.setText(M_DATA_SET.get(position));
        }

        @Override
        public int getItemCount() {
            return M_DATA_SET.size();
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvRecyclerItem;

            MyViewHolder(View itemView) {
                super(itemView);
                tvRecyclerItem = (TextView) itemView.findViewById(R.id.tv_recycler_item);
            }
        }
    }
}
