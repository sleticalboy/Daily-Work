package com.sleticalboy.dailywork.rv;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.weight.view.PagerView;
import com.sleticalboy.weight.xrecycler.adapter.XBaseHolder;
import com.sleticalboy.weight.xrecycler.adapter.XRecyclerAdapter;

/**
 * Created on 18-2-11.
 *
 * @author leebin
 * @version 1.0
 * @description 测试 RecyclerView  分页效果
 */
public class PagerActivity extends AppCompatActivity {

    private static final String TAG = "PagerActivity";

    private static int sCounter = 0;

    private Integer[] mImagesId = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setView();
        setLayout();
    }

    private void setLayout() {
        setContentView(R.layout.activity_pager);
        PagerView pagerView = findViewById(R.id.pager_view);
        pagerView.setTitle("已添加的应用");
        pagerView.setAdapter(new PagerAdapter(this, mImagesId));
        pagerView.setIndicatorDrawable(R.drawable.mx_page_indicator);

        findViewById(R.id.btnShowPage).setOnClickListener(v ->
                pagerView.setVisibility(++sCounter % 2 == 0 ? View.GONE : View.VISIBLE));
    }

    private void setView() {
        int rows = 3;
        int columns = 4;
        PagerView pagerView = new PagerView(this, rows, columns);
        pagerView.setIndicatorSize(4);
        pagerView.setIndicatorDrawable(R.drawable.mx_page_indicator);
        pagerView.setAdapter(new PagerAdapter(this, mImagesId));
        setContentView(pagerView);
    }

    static class PagerAdapter extends XRecyclerAdapter<Integer> {
        Integer[] mIntegers;

        public PagerAdapter(Context context, Integer[] integers) {
            super(context);
            mIntegers = integers;
        }

        @Override
        protected XBaseHolder onCreateItemHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(parent, R.layout.item_common_layout);
        }

        @Override
        public Integer getItemData(int position) {
//            position %= mIntegers.length;
            return mIntegers[position];
        }

        @Override
        public int getCount() {
            return mIntegers.length;
        }

        static class ItemHolder extends XBaseHolder<Integer> {
            ImageView mImageView;

            public ItemHolder(ViewGroup parent, int res) {
                super(parent, res);
                mImageView = getView(R.id.image_view);
                mImageView.setAdjustViewBounds(true);
            }

            @Override
            protected void setData(Integer data) {
                mImageView.setImageResource(data);
            }
        }
    }
}
