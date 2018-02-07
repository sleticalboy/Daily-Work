package com.sleticalboy.dailywork.weight.xrefresh.interfaces;

import android.view.View;

/**
 * Created on 18-2-3.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public interface ILoadView {

    /**
     * 开始加载
     */
    void begin();

    /**
     * 更新进度
     *
     * @param progress
     * @param total
     */
    void progress(long progress, long total);

    /**
     * 加载完成
     *
     * @param progress
     * @param total
     */
    void finish(long progress, long total);

    /**
     * 正在加载
     */
    void loading();

    /**
     * 隐藏
     */
    void hidden();

    /**
     * 获取当前视图
     *
     * @return 当前视图
     */
    View get();
}
