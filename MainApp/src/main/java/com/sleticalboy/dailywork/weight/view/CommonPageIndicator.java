package com.sleticalboy.dailywork.weight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 18-3-15.
 *
 * @author sleticalboy
 * @description 圆点页面指示器
 */
public class CommonPageIndicator extends View implements PageIndicator {

    private static final int INVALID_POINTER = -1;

    private int mRadius;
    private int mCurrentPage;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mInternalListener;

    public CommonPageIndicator(Context context) {
        this(context, null);
    }

    public CommonPageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mCurrentPage = mCurrentPage;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.mCurrentPage;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int specSize = View.MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 2 * mRadius + getPaddingTop() + getPaddingBottom() + 1;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int specSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY || mViewPager == null || mViewPager.getAdapter() == null) {
            result = specSize;
        } else {
            final int count = mViewPager.getAdapter().getCount();
            result = getPaddingLeft() + getPaddingRight() + 2 * count * mRadius + (count - 1) * mRadius + 1;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        int mCurrentPage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mCurrentPage = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mCurrentPage);
        }
    }

    @Override
    public void setupWithViewPager(ViewPager pagerView, int initialPos) {
        mViewPager = checkValid(mViewPager);
        if (mViewPager.getAdapter() == null) {
            throw new IllegalStateException("没有设置 Adapter");
        }
        mViewPager = pagerView;
        invalidate();
    }

    private <T> T checkValid(T ref) {
        if (ref == null) {
            throw new IllegalArgumentException();
        }
        return ref;
    }

    @Override
    public void setCurrentPage(int pageIndex) {
        mCurrentPage = pageIndex;
        invalidate();
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }
}
