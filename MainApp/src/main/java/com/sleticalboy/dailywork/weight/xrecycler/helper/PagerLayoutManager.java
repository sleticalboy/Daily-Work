package com.sleticalboy.dailywork.weight.xrecycler.helper;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 2016/11/9.
 *
 * @author zhuguohui
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager implements
        PageDecorationLastJudge {

    private static final String TAG = "PagerLayoutManager";

    private int totalHeight = 0;
    private int totalWidth = 0;
    private int offsetY = 0;
    private int offsetX = 0;

    private SparseArray<Rect> allItemFrames = new SparseArray<>();

    private int mRows = 0;
    private int mColumns = 0;
    private int mPageSize = 0;
    private int itemWidth = 0;
    private int itemHeight = 0;
    private int onePageSize = 0;
    private int itemWidthUsed;
    private int itemHeightUsed;
    private int[] mMeasuredDimension = new int[2];


    public PagerLayoutManager(int rows, int columns) {
        mRows = rows;
        mColumns = columns;
        this.onePageSize = rows * columns;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int newX = offsetX + dx;
        int result = dx;
        if (newX > totalWidth) {
            result = totalWidth - offsetX;
        } else if (newX < 0) {
            result = 0 - offsetX;
        }
        offsetX += result;
        offsetChildrenHorizontal(-result);
        recycleAndFillItems(recycler, state);
        return result;
    }

    private int getUsableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getUsableHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        if (state.isPreLayout()) {
            return;
        }
        //获取每个Item的平均宽高
        itemWidth = getUsableWidth() / mColumns;
        itemHeight = getUsableHeight() / mRows;

        //计算宽高已经使用的量，主要用于后期测量
        itemWidthUsed = (mColumns - 1) * itemWidth;
        itemHeightUsed = (mRows - 1) * itemHeight;

        //计算总的页数
        mPageSize = getPageSize();

        //计算可以横向滚动的最大值
        totalWidth = (mPageSize - 1) * getWidth();

        //分离view
        detachAndScrapAttachedViews(recycler);

        int count = getItemCount();
        for (int p = 0; p < mPageSize; p++) {
            for (int r = 0; r < mRows; r++) {
                for (int c = 0; c < mColumns; c++) {
                    int index = p * onePageSize + r * mColumns + c;
                    if (index == count) {
                        //跳出多重循环
                        c = mColumns;
                        r = mRows;
                        p = mPageSize;
                        break;
                    }

                    View view = recycler.getViewForPosition(index);
                    addView(view);
                    //测量item
                    measureChildWithMargins(view, itemWidthUsed, itemHeightUsed);

                    int width = getDecoratedMeasuredWidth(view);
                    int height = getDecoratedMeasuredHeight(view);
                    //记录显示范围
                    Rect rect = allItemFrames.get(index);
                    if (rect == null) {
                        rect = new Rect();
                    }
                    int x = p * getUsableWidth() + c * itemWidth;
                    int y = r * itemHeight;
                    rect.set(x, y, width + x, height + y);
                    allItemFrames.put(index, rect);
                }
            }
            //每一页循环以后就回收一页的View用于下一页的使用
            removeAndRecycleAllViews(recycler);
        }
        recycleAndFillItems(recycler, state);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        offsetX = 0;
        offsetY = 0;
    }

    private void recycleAndFillItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }
        Rect displayRect = new Rect(getPaddingLeft() + offsetX, getPaddingTop(),
                getWidth() - getPaddingLeft() - getPaddingRight() + offsetX,
                getHeight() - getPaddingTop() - getPaddingBottom());
        Rect childRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            childRect.left = getDecoratedLeft(child);
            childRect.top = getDecoratedTop(child);
            childRect.right = getDecoratedRight(child);
            childRect.bottom = getDecoratedBottom(child);
            if (!Rect.intersects(displayRect, childRect)) {
                removeAndRecycleView(child, recycler);
            }
        }

        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayRect, allItemFrames.get(i))) {
                View view = recycler.getViewForPosition(i);
                addView(view);
                measureChildWithMargins(view, itemWidthUsed, itemHeightUsed);
                Rect rect = allItemFrames.get(i);
                layoutDecorated(view, rect.left - offsetX, rect.top,
                        rect.right - offsetX, rect.bottom);
            }
        }
    }

    /**
     * 计算总页数
     *
     * @return 总页数
     */
    public int getPageSize() {
        final int itemCount = getItemCount();
        return itemCount / onePageSize + (itemCount % onePageSize == 0 ? 0 : 1);
    }

    @Override
    public boolean isLastRow(int index) {
        if (index >= 0 && index < getItemCount()) {
            int indexOfPage = index % onePageSize;
            indexOfPage++;
            if (indexOfPage > (mRows - 1) * mColumns && indexOfPage <= onePageSize) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLastColumn(int position) {
        if (position >= 0 && position < getItemCount()) {
            position++;
            if (position % mColumns == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLastLast(int position) {
        position++;
        return position % onePageSize == 0;
    }
}
