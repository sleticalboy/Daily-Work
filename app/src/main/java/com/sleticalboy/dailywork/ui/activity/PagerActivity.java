package com.sleticalboy.dailywork.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.util.UIUtils;
import com.sleticalboy.dailywork.weight.PagerView;
import com.sleticalboy.dailywork.weight.xrecycler.adapter.XBaseHolder;
import com.sleticalboy.dailywork.weight.xrecycler.adapter.XRecyclerAdapter;
import com.sleticalboy.dailywork.weight.xrecycler.helper.PageLayoutManager;
import com.sleticalboy.dailywork.weight.xrecycler.helper.PageScrollHelper;

/**
 * Created on 18-2-11.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 测试 RecyclerView  分页效果
 */
public class PagerActivity extends /*BaseActivity*/ AppCompatActivity implements
        PageScrollHelper.OnPageSelectedListener {

    private static final String TAG = "PagerActivity";

    private Integer[] mImagesId = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
    };

    private int mPageSize;
    private int mCurrentPage = 0;
    private LinearLayout llIndicators;
    private int indicatorWidth;
    private PageLayoutManager mPageLayoutManager;
    private int mRows = 3;
    private int mColumns = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setView();
        setLayout();
    }

    private void setView() {
        PagerView pagerView = new PagerView(this, mRows, mColumns);
        pagerView.setIndicatorSize(4);
        pagerView.setIndicatorDrawable(R.drawable.mx_page_indicator);
        pagerView.setAdapter(new PagerAdapter(this, mImagesId));
        setContentView(pagerView);
    }

    private void setLayout() {
        setContentView(R.layout.activity_pager);
        PagerView pagerView = findViewById(R.id.pager_view);
        pagerView.setAdapter(new PagerAdapter(this, mImagesId));
        pagerView.setIndicatorDrawable(R.drawable.mx_page_indicator);
    }

    //    @Override
    protected void initData() {

    }

//    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        llIndicators = findViewById(R.id.ll_indicators);
        mPageLayoutManager = new PageLayoutManager(mRows, mColumns);
        recyclerView.setLayoutManager(mPageLayoutManager);
        PagerAdapter adapter = new PagerAdapter(this, mImagesId);
        recyclerView.setAdapter(adapter);

        PageScrollHelper pageScrollHelper = new PageScrollHelper();
        pageScrollHelper.setUpWithRecycleView(recyclerView);
        pageScrollHelper.setOnPageSelectedListener(this);

        PagerView pagerView = new PagerView(this, 3, 4);
        pagerView.setAdapter(new PagerAdapter(this, mImagesId));
    }

//    @Override
    protected int attachLayout() {
        return R.layout.activity_pager;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        mPageSize = getPageSize();
//        mPageSize = mPageLayoutManager.getPageSize();
//        llIndicators.removeAllViews();
//        indicatorWidth = UIUtils.dp2px(this, 3);
//        for (int i = 0; i < mPageSize; i++) {
//            View indicator = new View(this);
//            if (mCurrentPage == i) {
//                indicator.setPressed(true);
//                indicator.setSelected(true);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        2 * indicatorWidth, 2 * indicatorWidth);
//                params.setMargins(2 * indicatorWidth, 0, 0, 0);
//                indicator.setLayoutParams(params);
//            } else {
//                indicator.setPressed(false);
//                indicator.setSelected(false);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        indicatorWidth, indicatorWidth);
//                params.setMargins(2 * indicatorWidth, 0, 0, 0);
//                indicator.setLayoutParams(params);
//            }
//            indicator.setBackgroundResource(R.drawable.mx_page_indicator);
//            llIndicators.addView(indicator);
//        }
    }

    private int getPageSize() {
        final int onePageSize = mRows * mColumns;
        final int itemCount = mPageLayoutManager.getItemCount();
        return itemCount / onePageSize + (itemCount % onePageSize == 0 ? 0 : 1);
    }

    @Override
    public void onPageChanged(int pageIndex) {
        mCurrentPage = pageIndex;
        for (int i = 0; i < mPageSize; i++) {
            View view = llIndicators.getChildAt(i);
            if (mCurrentPage == i) {
                view.setSelected(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        2 * indicatorWidth, 2 * indicatorWidth);
                params.setMargins(2 * indicatorWidth, 0, 0, 0);
                view.setLayoutParams(params);
            } else {
                view.setSelected(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        indicatorWidth, indicatorWidth);
                params.setMargins(2 * indicatorWidth, 0, 0, 0);
                view.setLayoutParams(params);
            }
        }
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
            position %= mIntegers.length;
            return mIntegers[position];
        }

        @Override
        public int getCount() {
            return 300;
        }
    }

    static class ItemHolder extends XBaseHolder<Integer> {

        ImageView mImageView;

        public ItemHolder(ViewGroup parent, int res) {
            super(parent, res);
            mImageView = getView(R.id.image_view);
        }

        @Override
        protected void setData(Integer data) {
            mImageView.setImageResource(data);
        }
    }
}
