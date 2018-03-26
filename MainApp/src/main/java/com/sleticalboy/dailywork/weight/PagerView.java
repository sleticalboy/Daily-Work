package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.util.UIUtils;
import com.sleticalboy.dailywork.weight.xrecycler.helper.PageScrollHelper;
import com.sleticalboy.dailywork.weight.xrecycler.helper.PagerLayoutManager;

/**
 * 使用 RecyclerView 实现的 ViewPager，支持单页翻动，支持自适应高度
 */
public class PagerView extends LinearLayout {

    private static final String TAG = "PagerView";
    private static final int DEFAULT_ROWS = 1;
    private static final int DEFAULT_COLUMNS = DEFAULT_ROWS;
    private static final float INDICATOR_SIZE = 3.0f;
    private static final int INDICATOR_DRAWABLE = R.drawable.mx_page_indicator;
    private static final int MAX_PAGE_SIZE = 25;
    private static final int DEFAULT_INDICATOR_LAYOUT_GRAVITY = Gravity.END;

    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private LinearLayout mIndicatorLayout;

    private int mRows;
    private int mColumns;
    private int mIndicatorSize = (int) INDICATOR_SIZE;
    private int mIndicatorDrawableResId = INDICATOR_DRAWABLE;
    private int mPageSize;
    private int mCurrentPage;
    private PageScrollHelper.OnPageSelectedListener mOnPageSelectedListener;

    public PagerView(Context context, int rows, int columns) {
        this(context);
        setRowsAndColumns(rows, columns);
    }

    public PagerView(Context context) {
        this(context, null);
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

    public PagerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initView();
        initFromAttrs(attrs);
        ensurePageSize();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_pager_view_layout, this);
        mTextView = findViewById(R.id.tv_title);
        mRecyclerView = findViewById(R.id.recycler_view);
        mIndicatorLayout = findViewById(R.id.ll_indicators);
    }

    private void initFromAttrs(@Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, R.styleable.PagerView);
            mRows = a.getInteger(R.styleable.PagerView_page_rows, DEFAULT_ROWS);
            mColumns = a.getInteger(R.styleable.PagerView_page_columns, DEFAULT_COLUMNS);
            mIndicatorSize = a.getDimensionPixelSize(
                    R.styleable.PagerView_page_indicator_size, (int) INDICATOR_SIZE);
            mIndicatorDrawableResId = a.getResourceId(
                    R.styleable.PagerView_page_indicator_drawable, INDICATOR_DRAWABLE);
            int gravity = a.getLayoutDimension(
                    R.styleable.PagerView_page_indicator_gravity, DEFAULT_INDICATOR_LAYOUT_GRAVITY);
            if (gravity > 0) {
                setIndicatorLayoutGravity(gravity);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    private void ensurePageSize() {
        calc();
    }

    public void setIndicatorLayoutGravity(int gravity) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mIndicatorLayout.getLayoutParams();
        lp.gravity = gravity;
        mIndicatorLayout.setLayoutParams(lp);
    }

    private void calc() {
        if (getLayoutManager() == null) {
            return;
        }
        if (getLayoutManager() instanceof PagerLayoutManager) {
            int pageSize = ((PagerLayoutManager) getLayoutManager()).getPageSize();
            mPageSize = pageSize > MAX_PAGE_SIZE ? MAX_PAGE_SIZE : pageSize;
        }
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        if (mRecyclerView == null || mRecyclerView.getLayoutManager() == null) {
            return null;
        }
        return mRecyclerView.getLayoutManager();
    }

    private void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecyclerView == null || mRecyclerView.getLayoutManager() != null) {
            return;
        }
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        // 初始化 indicators
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

    public void setIndicatorSize(int indicatorSize) {
        if (indicatorSize < INDICATOR_SIZE) {
            mIndicatorSize = (int) INDICATOR_SIZE;
        } else {
            mIndicatorSize = UIUtils.INSTANCE.dp2px(getContext(), indicatorSize);
        }
    }

    public void setIndicatorDrawable(int indicatorDrawableResId) {
        if (indicatorDrawableResId < 0) {
            mIndicatorDrawableResId = INDICATOR_DRAWABLE;
        } else {
            mIndicatorDrawableResId = indicatorDrawableResId;
        }
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTextView.setText(title);
            mTextView.setVisibility(VISIBLE);
        }
    }

    public int getPageSize() {
        return mPageSize;
    }

    public void scrollToPage(int pageIndex) {
        mCurrentPage = pageIndex;
    }

    public RecyclerView.Adapter getAdapter() {
        if (mRecyclerView == null || mRecyclerView.getAdapter() == null) return null;
        return mRecyclerView.getAdapter();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mRecyclerView == null || adapter == null) {
            return;
        }
        if (mRecyclerView.getAdapter() != null) {
            return;
        }
        mRecyclerView.setAdapter(adapter);
        setLayoutManager(new PagerLayoutManager(mRows, mColumns));
        setScrollHelper(new PageScrollHelper());
        ensurePageSize();
    }

    private void setScrollHelper(PageScrollHelper helper) {
        if (mRecyclerView == null || helper == null) {
            return;
        }
        // 将 ScrollHelper 与 RecyclerView 关联起来
        helper.setUpWithRecycleView(mRecyclerView);
        if (mOnPageSelectedListener == null) {
            mOnPageSelectedListener = new SimpleOnPageSelectedListener();
        }
        helper.setOnPageSelectedListener(mOnPageSelectedListener);
    }

    public void setOnPageSelectedListener(PageScrollHelper.OnPageSelectedListener onPageSelectedListener) {
        mOnPageSelectedListener = onPageSelectedListener;
    }

    public class SimpleOnPageSelectedListener implements PageScrollHelper.OnPageSelectedListener {

        @Override
        public void onPageChanged(int pageIndex) {
            mCurrentPage = pageIndex % mPageSize;
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
