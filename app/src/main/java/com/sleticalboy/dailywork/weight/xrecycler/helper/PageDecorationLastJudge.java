package com.sleticalboy.dailywork.weight.xrecycler.helper;

/**
 * Created on 2016/11/15.
 * @author zhuguohui
 */
public interface PageDecorationLastJudge {
    /**
     * Is the last row in one page
     *
     * @param position
     * @return
     */
    boolean isLastRow(int position);

    /**
     * Is the last Colum in one row;
     *
     * @param position
     * @return
     */
    boolean isLastColumn(int position);

    boolean isLastLast(int position);
}
