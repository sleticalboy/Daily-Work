package com.sleticalboy.dailywork.weight.view;

/**
 * Created on 18-3-15.
 *
 * @author sleticalboy
 * @description 页面指示器
 */
public interface PageIndicator {

    void setWithPagerView(PagerView pagerView);

    void setWithPagerView(PagerView pagerView, int initialPos);

    void setCurrentPage(int pageIndex);

    void notifyDataSetChanged();
}
