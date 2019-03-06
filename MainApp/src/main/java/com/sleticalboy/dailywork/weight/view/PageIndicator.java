package com.sleticalboy.dailywork.weight.view;

import androidx.viewpager.widget.ViewPager;

/**
 * Created on 18-3-15.
 *
 * @author leebin
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
