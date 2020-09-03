package com.sleticalboy.weight.xrefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sleticalboy.learning.R;

/**
 * Created on 18-2-3.
 *
 * @author leebin
 * @version 1.0
 * @description
 */
public class XRefreshView extends FrameLayout {

    private static final String TAG = "XRefreshView";

    private ViewGroup mEmptyView;
    private ViewGroup mErrorView;
    private ViewGroup mProgressView;
    private int mEmptyId;
    private int mProgressId;
    private int mErrorId;
    private Context mContext;

    public XRefreshView(@NonNull Context context) {
        this(context, null);
    }

    public XRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initView();
        initAttrs(attrs);
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XRefreshView);
        try {
            mEmptyId = a.getResourceId(R.styleable.XRefreshView_layout_empty, 0);
            mErrorId = a.getResourceId(R.styleable.XRefreshView_layout_error, 0);
            mProgressId = a.getResourceId(R.styleable.XRefreshView_layout_progress, 0);
        } finally {
            a.recycle();
        }
    }

    public void setEmptyView(View emptyView) {
        mEmptyView.removeAllViews();
        mEmptyView.addView(emptyView);
    }

    public void setEmptyView(int emptyView) {
        mEmptyView.removeAllViews();
        View.inflate(mContext, emptyView, mEmptyView);
    }

    public void setErrorView(View errorView) {
        mErrorView.removeAllViews();
        mErrorView.addView(errorView);
    }

    public void setErrorView(int errorView) {
        mErrorView.removeAllViews();
        View.inflate(mContext, errorView, mErrorView);
    }

    public void setProgressView(View progressView) {
        mProgressView.removeAllViews();
        mProgressView.addView(progressView);
    }

    public void setProgressView(int progressView) {
        mProgressView.removeAllViews();
        View.inflate(mContext, progressView, mProgressView);
    }
}
