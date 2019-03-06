package com.sleticalboy.dailywork.weight.xrefresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.sleticalboy.dailywork.util.UIUtils;
import com.sleticalboy.dailywork.weight.xrefresh.interfaces.IFooterView;
import com.sleticalboy.dailywork.weight.xrefresh.interfaces.IHeaderView;

import androidx.core.view.ViewCompat;

/**
 * Created on 18-2-2.
 *
 * @author leebin
 * @version 1.0
 * @description 提供下拉刷新和上拉加载的 layout
 */
public class XRefreshLayout extends FrameLayout {

    private static final String TAG = "XRefreshLayout";

    private static final int ANIM_TIME = 180;
    private static final int DEFAULT_HEADER_HEIGHT = 60;
    private static final int DEFAULT_FOOTER_HEIGHT = 60;

    public static final int STATE_REFRESH = 0x001;
    public static final int STATE_LOAD = 0x002;

    //----------- 外部可以控制的属性------------------
    private boolean mCanPullDown = true;    // 是否可以下拉
    private boolean mCanPullUp = true;      // 是否可以上拉
    private int mHeaderHeight = DEFAULT_HEADER_HEIGHT; // header 高度
    private int mFooterHeight = DEFAULT_FOOTER_HEIGHT; // footer 高度
    private int mMaxHeaderHeight;   // header 最大高度
    private int mMaxFooterHeight;   // footer 最大高度
    private IHeaderView mHeaderView; // header 视图
    private IFooterView mFooterView; // footer 视图
    OnLoadMoreListener mLoadMoreListener; // 上拉/下拉加载监听
    //----------- 外部可以控制的属性------------------

    //----------- 内部使用的属性------------------
    private int mTouchSlop;
    private Scroller mScroller;
    private View mTarget; // the target of the gesture
    private float mTouchY;
    private float mLastY;
    //----------- 内部使用的属性------------------

    private static final int[] LAYOUT_ATTRS = {
            android.R.attr.paddingLeft, android.R.attr.paddingRight,
            android.R.attr.paddingTop, android.R.attr.paddingBottom,
    };
    private boolean mIsPullUp = false;
    private boolean mIsPullDown = false;

    public XRefreshLayout(Context context) {
        this(context, null);
    }

    public XRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initView();
        initFromAttrs(attrs);
    }

    private void init() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        Log.d(TAG, "mTouchSlop:" + mTouchSlop);
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
    }

    private void initFromAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addHeader();
        addFooter();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    private void reset() {
        // release views
    }

    private void addHeader() {
        if (mHeaderView == null) {
            mHeaderView = new HeaderView(getContext());
        }
        View header = mHeaderView.get();
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        header.setLayoutParams(p);
        if (header.getParent() != null) {
            ((ViewGroup) header.getParent()).removeView(header);
        }
        addView(header, 0);
    }

    private void addFooter() {
        if (mFooterView == null) {
            mFooterView = new FooterView(getContext());
        }
        View footer = mFooterView.get();
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        p.gravity = Gravity.BOTTOM;
        footer.setLayoutParams(p);
        if (footer.getParent() != null) {
            ((ViewGroup) footer.getParent()).removeView(footer);
        }
        addView(footer);
    }

    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!(child instanceof IFooterView) && !(child instanceof IHeaderView)) {
                    mTarget = child;
                    break;
                }
            }
        }
        Log.d(TAG, "mTarget:" + mTarget);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure() called with: widthMeasureSpec = [" + widthMeasureSpec
                + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(
                MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout() called with: changed = [" + changed + "], l = [" + l
                + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
        super.onLayout(changed, l, t, r, b);

        // -----------header---------
        // left--------top-------- right
        //     |                 |
        //     |                 |
        //     |                 |
        //     |                 |
        // left------bottom------- right
        // -----------footer--------
    }

    @Override
    protected void onFinishInflate() {
        Log.d(TAG, "onFinishInflate() called");
        super.onFinishInflate();
        if (getChildCount() != 1) {
            throw new IllegalStateException("child view must be only one");
        }
        ensureTarget();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /*if (onInterceptTouchEvent(ev)) { // 如果当前 view 拦截了事件
            return onTouchEvent(ev); // 返回当前 view 对事件的处理结果
        } else { // 如果当前 view 没有拦截事件
            return mTarget.dispatchTouchEvent(ev); // 返回 child 对事件的分发结果
        }*/
        boolean dispatched = false;
        if (onInterceptTouchEvent(ev)) {
            dispatched = onTouchEvent(ev);
        } else {
            dispatched = mTarget.dispatchTouchEvent(ev);
        }
        return dispatched;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                Log.d(TAG, "onInterceptTouchEvent: action down");
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - mTouchY;
                if (deltaY >= 0 && deltaY > mTouchSlop) {
                    Log.d(TAG, "intercept pull down event");
                    mIsPullDown = true;
//                    return true;
                } else if (deltaY < 0 && -deltaY > mTouchSlop) {
                    Log.d(TAG, "intercept pull up event");
                    mIsPullUp = true;
//                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastY = ev.getY();
                Log.d(TAG, "onInterceptTouchEvent: action up");
                break;
            case MotionEvent.ACTION_CANCEL:
                mLastY = ev.getY();
                Log.d(TAG, "onInterceptTouchEvent: action cancel");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = event.getY();
                Log.d(TAG, "onTouchEvent: action down");
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = getY() - mTouchY;
                if (deltaY >= 0 && deltaY > mTouchSlop) {
                    Log.d(TAG, "pull down ....");
                    if (mIsPullDown) {
//                        return true;
                    } else {
//                        return mTarget.onTouchEvent(event);
                    }
                } else if (deltaY < 0 && -deltaY > mTouchSlop) {
                    Log.d(TAG, "pull up ....");
                    if (mIsPullUp) {
//                        return true;
                    } else {
//                        return mTarget.onTouchEvent(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastY = event.getY();
                Log.d(TAG, "onTouchEvent: action up");
                break;
            case MotionEvent.ACTION_CANCEL:
                mLastY = event.getY();
                Log.d(TAG, "onTouchEvent: action cancel");
                break;
        }
        return super.onTouchEvent(event);
    }

    private void createTranslationYAnim(final int state, final int from, final int to) {
        Log.d(TAG, "create animation");
        final ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(ANIM_TIME);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                Log.d(TAG, "value:" + value);
                requestLayout();
            }
        });
        animator.start();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int deltaX = destX - scrollX;
        mScroller.startScroll(scrollX, 0, deltaX, 500);
    }

    /**
     * @return Return it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    private boolean canChildScroll(int direction) {
        if (direction == 0) {
            throw new IllegalArgumentException("direction only can be negative or positive value: " + direction);
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 &&
                        (absListView.getFirstVisiblePosition() > 0
                                || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, direction) || mTarget.getScrollY() != 0;
            }
        } else {
            return mTarget.canScrollVertically(direction);
        }
    }

    public boolean canChildScrollDown() {
        return canChildScroll(1);
    }

    public boolean canChildScrollUp() {
        return canChildScroll(-1);
    }

    /**
     * 上拉/下拉加载更多监听
     */
    public interface OnLoadMoreListener {

        /**
         * 上拉加载时回调
         */
        void onPullUp();

        /**
         * 下拉加载时回调
         */
        void onPullDown();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setHeaderView(IHeaderView headerView) {
        mHeaderView = headerView;
    }

    public void setFooterView(IFooterView footerView) {
        mFooterView = footerView;
    }

    public void setHeaderHeight(int headerHeight) {
        mHeaderHeight = UIUtils.INSTANCE.dp2px(getContext(), headerHeight);
    }

    public void setFooterHeight(int footerHeight) {
        mFooterHeight = UIUtils.INSTANCE.dp2px(getContext(), footerHeight);
    }

    public boolean isCanPullDown() {
        return mCanPullDown;
    }

    public void setCanPullDown(boolean canPullDown) {
        mCanPullDown = canPullDown;
    }

    public boolean isCanPullUp() {
        return mCanPullUp;
    }

    public void setCanPullUp(boolean canPullUp) {
        mCanPullUp = canPullUp;
    }
}
