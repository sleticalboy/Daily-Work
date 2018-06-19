package com.sleticalboy.dailywork.weight.view;

import android.support.v4.view.ViewPager;

/**
 * Created on 18-3-15.
 *
 * @author sleticalboy
 * @description 页面指示器
 */
public interface PageIndicator {

    /**
     * @param viewPager
     * @param initialPos
     */
    void setupWithViewPager(ViewPager viewPager, int initialPos);

    /**
     * @param pageIndex
     */
    void setCurrentPage(int pageIndex);

    /**
     *
     */
    void notifyDataSetChanged();
}
