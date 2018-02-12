package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.util.UIUtils;
import com.sleticalboy.dailywork.weight.xrecycler.helper.PageLayoutManager;
import com.sleticalboy.dailywork.weight.xrecycler.helper.PageScrollHelper;

/**
 * Created on 18-2-11.
 * Usage:
 * <pre>
 *   PagerView pagerView = new PagerView(context, rows, columns);
 *   pagerView.setIndicatorSize(4);
 *   pagerView.setIndicatorDrawable(R.drawable.indicator);
 *   pagerView.setAdapter(adapter);
 * </pre>
 * or
 * <pre>
 *     <com.sleticalboy.dailywork.weight.PagerView
 *      xmlns:android="http://schemas.android.com/apk/res/android"
 *      xmlns:app="http://schemas.android.com/apk/res-auto"
 *      android:id="@+id/pager_view"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"
 *      app:indicator_drawable="@drawable/mx_page_indicator"
 *      app:indicator_size="3dp"
 *      app:layout_columns="4"
 *      app:layout_rows="3"/>
 * </pre>
 *
 * @author sleticalboy
 * @version 1.0
 */
public class PagerView extends FrameLayout {

    private static final String TAG = "PagerView";
    private static final int DEFAULT_ROWS = 1;
    private static final int DEFAULT_COLUMNS = DEFAULT_ROWS;
    private static final float INDICATOR_SIZE = 3.0f;
    private static final int INDICATOR_DRAWABLE = R.drawable.mx_page_indicator;

    private RecyclerView mRecyclerView;
    private LinearLayout mIndicatorLayout;

    private int mRows;
    private int mColumns;
    private int mIndicatorSize = (int) INDICATOR_SIZE;
    private int mIndicatorDrawableResId = INDICATOR_DRAWABLE;
    private int mPageSize;
    private int mCurrentPage;

    public PagerView(Context context, int rows, int columns) {
        this(context);
        setRowsAndColumns(rows, columns);
    }

    public PagerView(Context context) {
        this(context, null);
    }

    public PagerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttrs(attrs);
        initView();
        ensurePageSize();
    }

    private void initFromAttrs(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.PagerView);
            mRows = a.getInteger(R.styleable.PagerView_layout_rows, DEFAULT_ROWS);
            mColumns = a.getInteger(R.styleable.PagerView_layout_columns, DEFAULT_COLUMNS);
            mIndicatorSize = a.getDimensionPixelSize(R.styleable.PagerView_indicator_size, (int) INDICATOR_SIZE);
            mIndicatorDrawableResId = a.getResourceId(R.styleable.PagerView_indicator_drawable, INDICATOR_DRAWABLE);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    // might not be processed
    private void calc() {
        if (getLayoutManager() == null) {
            return;
        }
        if (getLayoutManager() instanceof PageLayoutManager) {
            mPageSize = ((PageLayoutManager) getLayoutManager()).getPageSize();
        }
    }

    // ensure the page size was calculated
    private void ensurePageSize() {
        calc();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_pager_view_layout, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.ll_indicators);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPageSize == 0) {
            ensurePageSize();
        }
        if (mPageSize == 0) {
            return;
        }
        mIndicatorLayout.removeAllViews();
        for (int i = 0; i < mPageSize; i++) {
            View indicator = new ImageView(getContext());
            if (mCurrentPage == i) {
                indicator.setPressed(true);
                indicator.setSelected(true);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        2 * mIndicatorSize, 2 * mIndicatorSize);
                params.setMargins(2 * mIndicatorSize, 0, 0, 0);
                indicator.setLayoutParams(params);
            } else {
                indicator.setPressed(false);
                indicator.setSelected(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        mIndicatorSize, mIndicatorSize);
                params.setMargins(2 * mIndicatorSize, 0, 0, 0);
                indicator.setLayoutParams(params);
            }
            indicator.setBackgroundResource(mIndicatorDrawableResId);
            mIndicatorLayout.addView(indicator);
        }
    }

    public void setRowsAndColumns(int rows, int columns) {
        if (rows <= 0) {
            mRows = DEFAULT_ROWS;
        } else {
            mRows = rows;
        }
        if (columns <= 0) {
            mColumns = DEFAULT_COLUMNS;
        } else {
            mColumns = columns;
        }
    }

    public void setIndicatorSize(int indicatorSize) {
        if (indicatorSize < INDICATOR_SIZE) {
            mIndicatorSize = (int) INDICATOR_SIZE;
        } else {
            mIndicatorSize = UIUtils.dp2px(getContext(), indicatorSize);
        }
    }

    public void setIndicatorDrawable(int indicatorDrawableResId) {
        if (indicatorDrawableResId < 0) {
            mIndicatorDrawableResId = INDICATOR_DRAWABLE;
        }
        mIndicatorDrawableResId = indicatorDrawableResId;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mRecyclerView == null || adapter == null) {
            return;
        }
        if (mRecyclerView.getAdapter() != null) {
            return;
        }
        setLayoutManager(new PageLayoutManager(mRows, mColumns));
        mRecyclerView.setAdapter(adapter);
        setScrollHelper(new PageScrollHelper());
        ensurePageSize();
    }

    private void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecyclerView == null || mRecyclerView.getLayoutManager() != null) {
            return;
        }
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void setScrollHelper(PageScrollHelper helper) {
        if (mRecyclerView == null || helper == null) {
            return;
        }
        helper.setUpWithRecycleView(mRecyclerView);
        helper.setOnPageSelectedListener(new SimpleOnPageSelectedListener());
    }

    public RecyclerView.Adapter getAdapter() {
        if (mRecyclerView == null || mRecyclerView.getAdapter() == null) {
            return null;
        }
        return mRecyclerView.getAdapter();
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        if (mRecyclerView == null || mRecyclerView.getLayoutManager() == null) {
            return null;
        }
        return mRecyclerView.getLayoutManager();
    }

    class SimpleOnPageSelectedListener implements PageScrollHelper.OnPageSelectedListener {

        @Override
        public void onPageChanged(int pageIndex) {
            mCurrentPage = pageIndex;
            for (int i = 0; i < mPageSize; i++) {
                View view = mIndicatorLayout.getChildAt(i);
                if (mCurrentPage == i) {
                    view.setSelected(true);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            2 * mIndicatorSize, 2 * mIndicatorSize);
                    params.setMargins(2 * mIndicatorSize, 0, 0, 0);
                    view.setLayoutParams(params);
                } else {
                    view.setSelected(false);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            mIndicatorSize, mIndicatorSize);
                    params.setMargins(2 * mIndicatorSize, 0, 0, 0);
                    view.setLayoutParams(params);
                }
            }
        }
    }
}
