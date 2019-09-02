package com.binlee.emoji.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Created on 19-7-19.
 * <p>
 * 7-22 完工
 *
 * @author leebin
 */
public final class EmojiIndicator extends View {
    
    private static final String TAG = "EmojiIndicator";
    /**
     * 当页数超过此限制时指示器切换成 SeekBar 样式
     */
    private static final int SHOW_SEEK_BAR_PAGE_LIMIT = 10;
    /**
     * 指示器尺寸，即圆的半径
     */
    private static final float DOT_SIZE = 8;
    /**
     * 指示器之间间距
     */
    private static final float DOT_SPACING = DOT_SIZE * 4;
    /**
     * 静态指示器颜色
     */
    private static final int STATIC_DOT_COLOR = Color.parseColor("#ffa8a8a8");
    /**
     * 动态指示器颜色
     */
    private static final int DOT_COLOR = Color.parseColor("#ff646464");
    /**
     * 动态指示器圆心位置
     */
    private float mCx, mCy;
    private float mStartX;
    /**
     * 指示器间距
     */
    private float mSectionSize = DOT_SIZE * 6;
    private final Paint mPaint = new Paint();
    
    /**
     * 页数
     */
    private int mCount;
    /**
     * 是否有最近使用表情
     */
    private boolean mHasRecently = false;
    private ViewPager mAttachedVp;
    
    public EmojiIndicator(Context context) {
        this(context, null);
    }
    
    public EmojiIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public EmojiIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
    }
    
    @Override
    protected void onDraw(final Canvas canvas) {
        if (mCount == 0) {
            return;
        }
        checkInit();
        if (mCount >= SHOW_SEEK_BAR_PAGE_LIMIT) {
            drawSeekBar(canvas);
        } else {
            drawIndicator(canvas);
        }
    }
    
    private void drawSeekBar(final Canvas canvas) {
        // 绘制线
        mPaint.setColor(STATIC_DOT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DOT_SIZE / 2);
        canvas.drawLine(mStartX, getHeight() / 2, getWidth() - mStartX, getHeight() / 2, mPaint);
        // 绘制活动 indicator 间距，尺寸（比静态的大3个像素），颜色;（随着 ViewPager 滑动, 位置实时更新）
        // 0---1---2---3---④---⑤---6---7---8---9
        // 绘制表盘
        mPaint.setColor(DOT_COLOR);
        mPaint.setStyle(Paint.Style.FILL);
        mCx = Math.max(mCx, mStartX);
        canvas.drawCircle(mCx, mCy, DOT_SIZE + 3, mPaint);
        // 不知为何有这么一种 case： mCx: 128.00006, mStartX: 128.0， 导致表盘指针画不出来
        // 所以：Math.abs(mCx - mStartX) <= 0.1
        // if (mHasRecently && mCx == mStartX) {
        if (mHasRecently && Math.abs(mCx - mStartX) <= 0.1) {
            // 绘制表盘指针
            drawDialPointer(canvas);
        }
    }
    
    private void drawIndicator(final Canvas canvas) {
        // 绘制起点圆心 cx = (View 宽度 - (个数 × (静态尺寸 * 2 + 间距) - 间距)) / 2 + 尺寸
        // 绘制起点圆心 cy = View 高度 / 2
        // 〇---〇---〇---〇---〇---〇---〇---〇---〇---〇
        // 静止 indicator 个数，间距，尺寸，颜色（需要考虑最近使用表情）
        for (int i = 0; i < mCount; i++) {
            if (i == 0 && mHasRecently) {
                // 绘制最近使用标识：绘制表盘
                mPaint.setColor(STATIC_DOT_COLOR);
                canvas.drawCircle(mStartX, mCy, DOT_SIZE + 3, mPaint);
                // 绘制表盘指针
                drawDialPointer(canvas);
            } else {
                mPaint.setColor(STATIC_DOT_COLOR);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mStartX + i * mSectionSize, mCy, DOT_SIZE, mPaint);
            }
        }
        
        // 绘制活动 indicator 间距，尺寸（比静态的大3个像素），颜色;（随着 ViewPager 滑动, 位置实时更新）
        mPaint.setColor(DOT_COLOR);
        canvas.drawCircle(mCx <= DOT_SIZE ? mStartX : mCx, mCy, DOT_SIZE + 3, mPaint);
    }
    
    private void drawDialPointer(final Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DOT_SIZE / 4);
        canvas.drawLine(mStartX, mCy - DOT_SIZE, mStartX, mCy, mPaint);
        final float deltaX = (float) (Math.tan(Math.PI / 6) * DOT_SIZE);
        final float deltaY = (float) (Math.sin(Math.PI / 6) * DOT_SIZE);
        canvas.drawLine(mStartX, mCy, mStartX + deltaX, mCy + deltaY, mPaint);
    }
    
    private void checkInit() {
        if (mStartX != 0) {
            return;
        }
        mCy = getHeight() * 1F / 2;
        if (mCount >= SHOW_SEEK_BAR_PAGE_LIMIT) {
            mStartX = DOT_SPACING * 4;
            final float endX = getWidth() - mStartX;
            mSectionSize = (endX - mStartX) / (mCount - 1);
        } else {
            mSectionSize = DOT_SIZE * 6;
            mStartX = (getWidth() - (mCount * mSectionSize - DOT_SPACING)) / 2F + DOT_SIZE;
        }
    }
    
    public void attachViewPager(final ViewPager vp, final boolean hasRecently) {
        if (vp.getAdapter() == null) {
            return;
        }
        mCount = vp.getAdapter().getCount();
        mHasRecently = hasRecently;
        mAttachedVp = vp;
        // 分页指示器与 ViewPager 联动
        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset,
                                       final int positionOffsetPixels) {
                mCx = mStartX + (position + positionOffset) * mSectionSize;
                invalidate();
            }
            
            @Override
            public void onPageSelected(final int position) {
                mCx = mStartX + position * mSectionSize;
                invalidate();
            }
        });
        // 显示上次退出时的页面，但是再次进入时初始值还没初始化话，所以用了 post
        post(() -> {
            checkInit();
            mCx = mStartX + vp.getCurrentItem() * mSectionSize;
            invalidate();
        });
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final boolean isValid = isValidTouch(event.getX(), event.getY());
        if (mCount <= 1 || !isValid) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setClosestItem(event.getX(), event.getY());
                break;
        }
        return true;
    }
    
    private void setClosestItem(final float x, final float y) {
        if (!isValidTouch(x, y)) {
            return;
        }
        final int position = pointToIndex(x);
        if (position == -1) {
            return;
        }
        mAttachedVp.setCurrentItem(position);
    }
    
    private boolean isValidTouch(final float x, final float y) {
        // 边界值： 左 右 （左右范围各增大3个指示器尺寸大小）
        final float l = mStartX - DOT_SIZE * 3, r = getWidth() - l;
        // 边界值： 上 下
        final float t = 0, b = getHeight();
        Log.d(TAG, "isValidTouch: {l: " + l + ", t: " + t + ", r: " + r + ", b: " + b + ", x: " + x + ", y: " + y + "}");
        return x > l && x < r && y > t && y < b;
    }
    
    private int pointToIndex(final float x) {
        // 触摸点落在哪个指示器范围内就返回哪个指示器的索引
        int index = -1;
        // 校正值：
        final float adjustValue = mSectionSize / 2;
        for (int i = 0; i < mCount; i++) {
            // 边界值： 左 右 （左右范围各增大3个指示器尺寸大小）
            final float pxl = mStartX + i * mSectionSize - adjustValue;
            final float pxr = pxl + adjustValue * 2;
            // 边界值：上 下 暂时先不考虑
            // final float pyt = mCy - DOT_SIZE;
            // final float pyb = mCy + DOT_SIZE;
            if (x > pxl && x < pxr /*&& y > pyt && y < pyb*/) {
                index = i;
                Log.d(TAG, "pointToIndex() index: " + i + ", {l: " + pxl + ", x: " + x + ", r: " + pxr + "}");
                break;
            }
        }
        Log.d(TAG, "finally we returned: " + index + ", x: " + x);
        return index;
    }
    
    @Override
    public void setOnClickListener(@Nullable final OnClickListener l) {
        // empty implementation
    }
    
    @Override
    public void setOnLongClickListener(@Nullable final OnLongClickListener l) {
        // empty implementation
    }
}
