package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;

/**
 * Created on 18-2-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 下拉/上拉刷新 View
 */
public class PullRefreshView extends LinearLayout {

    private static final String TAG = "PullRefreshLayout";

    private static final int ANIM_TIME = 180;
    private static final float SCROLL_RATIO = 0.5f;
    public static final int STATE_NORMAL = 0x000;
    public static final int STATE_READY = 0x001;
    public static final int STATE_REFRESHING = 0x002;

    /**
     * pull down
     */
    public static final int PULL_DOWN = 0x003;
    /**
     * pull up
     */
    public static final int PULL_UP = 0x004;

    private Point mTouchPoint = new Point();
    private Rect mHeaderRect = new Rect();
    private Rect mContentRect = new Rect();
    private Rect mFooterRect = new Rect();

    private ImageView mArrowView;
    private ProgressBar mProgressBar;
    private TextView mHintView;
    private TextView mRefreshTimeView;

    private Animation mUpAnim;
    private Animation mDownAnim;

    private ViewGroup mHeaderView;
    private FrameLayout mContentView;
    private ViewGroup mFooterView;

    private int mTouchSlop;
    private Scroller mScroller;

    private int mHeaderHeight;
    private int mContentHeight;
    private int mFooterHeight;
    private int mCurrentState = STATE_NORMAL;

    private boolean mIsReadyRefresh;
    private boolean mIsRefreshEnable;

    private OnRefreshListener mRefreshListener;

    public PullRefreshView(Context context) {
        this(context, null);
    }

    public PullRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Log.d(TAG, "init() called with: context = [" + context + "]");
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context, new DecelerateInterpolator());

        initView();
        initAnimation();
    }

    // init view
    private void initView() {
        if (isInEditMode()) {
            return;
        }
        // 生成主 View
        View v = inflate(getContext(), R.layout.custom_refresh_layout, this);

        mHeaderView = v.findViewById(R.id.refresh_header);
        mFooterView = v.findViewById(R.id.refresh_footer);
        mContentView = v.findViewById(R.id.refresh_container);

        initHeaderView();
        initContentView();
        initFooterView();
    }

    private void initHeaderView() {
        mArrowView = mHeaderView.findViewById(R.id.default_ptr_flip);
        mProgressBar = mHeaderView.findViewById(R.id.default_ptr_rotate);
        mHintView = mHeaderView.findViewById(R.id.refresh_tip);
        mRefreshTimeView = mHeaderView.findViewById(R.id.refresh_time);
    }

    private void initContentView() {
    }

    private void initFooterView() {
    }

    // init animation
    private void initAnimation() {
        mUpAnim = new RotateAnimation(0.0f, 180.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mUpAnim.setFillAfter(true);
        mUpAnim.setDuration(ANIM_TIME);

        mDownAnim = new RotateAnimation(180.0f, 360.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mDownAnim.setFillAfter(true);
        mDownAnim.setDuration(ANIM_TIME);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure() called with: widthMeasureSpec = [" + widthMeasureSpec
                + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout() called with: changed = [" + changed + "], l = [" + l
                + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
        super.onLayout(changed, l, t, r, b);

        mHeaderHeight = getHeaderHeight();
        mContentHeight = getContentHeight();
        mFooterHeight = getFooterHeight();
        Log.d(TAG, "mHeaderHeight:" + mHeaderHeight);
        Log.d(TAG, "mContentHeight:" + mContentHeight);
        Log.d(TAG, "mFooterHeight:" + mFooterHeight);

        // -----------header---------
        // left--------top-------- right
        //     |                 |
        //     |                 |
        //     |                 |
        //     |                 |
        // left------bottom------- right
        // -----------footer--------
        mHeaderView.layout(l, -mHeaderHeight, r, 0);
        mContentView.layout(l, 0, r, mContentHeight);
        mFooterView.layout(l, b, r, b + mFooterHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw() called");
        super.onDraw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        Log.d(TAG, "onFinishInflate() called");
        super.onFinishInflate();
        if (getChildCount() == 0)
            return;
        for (int i = 0; i < getChildCount(); i++) {
            Log.d(TAG, "getChildAt(" + i + "):" + getChildAt(i));
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent() called");

        if (mCurrentState == STATE_REFRESHING)
            return true;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchPoint.set(((int) ev.getX()), ((int) ev.getY()));
                mHeaderRect.set(mHeaderView.getLeft(), mHeaderView.getTop(),
                        mHeaderView.getRight(), mHeaderView.getBottom());
                mContentRect.set(mContentView.getLeft(), mContentView.getTop(),
                        mContentView.getRight(), mContentView.getBottom());
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) ev.getY();
                int deltaY = moveY - mTouchPoint.y;
                if (mIsRefreshEnable && deltaY > 0) {
                    // 处理下拉时的逻辑
                    if (Math.abs(deltaY) > mTouchSlop && is2Top()) {
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        return true;
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private boolean is2Top() {
        return mContentView.getScrollY() <= 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent() called");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchPoint.set(((int) event.getX()), ((int) event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (event.getY() - mTouchPoint.y);
                if (is2Top() && getScrollY() <= 0) {
                    animateView((int) (-deltaY * SCROLL_RATIO));
                }
                mTouchPoint.set(((int) event.getX()), ((int) event.getY()));
                return true;
            case MotionEvent.ACTION_UP:
                break;
            default:
                int scrollY = getScrollY();

                if (mCurrentState == STATE_REFRESHING && scrollY < 0
                        && Math.abs(scrollY) > mHeaderHeight) {
                    resetView(-scrollY - mHeaderHeight);
                    return true;
                } else if (mCurrentState == STATE_REFRESHING && scrollY < 0
                        && Math.abs(scrollY) < mHeaderHeight) {
                    return true;
                }

                if (scrollY < 0 && Math.abs(scrollY) < mHeaderHeight) {
                    returnView();
                } else if (scrollY < 0 && Math.abs(scrollY) > mHeaderHeight
                        && mCurrentState == STATE_READY) {
                    resetView(-scrollY - mHeaderHeight);
                    refreshing();
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }
                } else if (scrollY < 0 && Math.abs(scrollY) >= mHeaderHeight
                        && mCurrentState == STATE_NORMAL) {
                    returnView();
                }

                break;
        }
        return true;
    }

    private void returnView() {
        resetView(-getScrollY());
    }

    private void resetView(int dy) {
        mScroller.startScroll(0, getScrollY(), 0, dy);
        postInvalidate();
    }

    private void animateView(int delta) {
        if (getScrollY() <= 0 && getScrollY() - delta <= 0) {
            if (getScrollY() < 0 && Math.abs(getScrollY()) > mHeaderHeight) {
                if (mIsReadyRefresh) {
                    arrowUp();
                }
            } else {
                if (!mIsReadyRefresh) {
                    arrowDown();
                }
            }
            scrollBy(0, delta);
        } else {
            scrollTo(0, 0);
        }
    }

    private void arrowUp() {
        mProgressBar.setVisibility(GONE);
        mArrowView.setVisibility(VISIBLE);
        mArrowView.startAnimation(mUpAnim);
        mHintView.setText("松开以刷新");
        mIsReadyRefresh = false;
        mCurrentState = STATE_READY;
    }

    private void arrowDown() {
        mProgressBar.setVisibility(GONE);
        mArrowView.setVisibility(VISIBLE);
        mArrowView.startAnimation(mDownAnim);
        mHintView.setText("下拉以刷新");
        mIsReadyRefresh = true;
        mCurrentState = STATE_NORMAL;
    }

    private void refreshing() {
        mArrowView.clearAnimation();
        mArrowView.invalidate();
        mArrowView.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
        mCurrentState = STATE_REFRESHING;
    }

    @Override
    public void computeScroll() {
        Log.d(TAG, "computeScroll() called");
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public int getHeaderHeight() {
        return mHeaderView.getMeasuredHeight();
    }

    public int getContentHeight() {
        return mContentView.getMeasuredHeight();
    }

    public int getFooterHeight() {
        return mFooterView.getMeasuredHeight();
    }

    public boolean isRefreshEnable() {
        return mIsRefreshEnable;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        mIsRefreshEnable = refreshEnable;
    }

    public void setRefreshTime(String refreshTime) {
        mRefreshTimeView.setText(refreshTime);
    }

    /**
     * 下拉刷新监听
     */
    public interface OnRefreshListener {

        /**
         * 下拉刷新时回调
         */
        void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }
}
