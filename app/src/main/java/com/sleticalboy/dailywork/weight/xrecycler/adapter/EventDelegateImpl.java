package com.sleticalboy.dailywork.weight.xrecycler.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
class EventDelegateImpl implements EventDelegate {

    private final RecyclerView.Adapter mAdapter;

    public EventDelegateImpl(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void addData(int length) {
    }

    @Override
    public void clear() {
    }
}
