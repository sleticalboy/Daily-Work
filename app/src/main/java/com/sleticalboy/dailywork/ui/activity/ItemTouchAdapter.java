package com.sleticalboy.dailywork.ui.activity;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.util.ListUtils;
import com.sleticalboy.dailywork.weight.xrecycler.adapter.XBaseHolder;
import com.sleticalboy.dailywork.weight.xrecycler.adapter.XRecyclerAdapter;
import com.sleticalboy.dailywork.weight.xrecycler.helper.ItemTouchDragAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class ItemTouchAdapter extends XRecyclerAdapter<Integer> implements ItemTouchDragAdapter {

    private Integer[] mObjects;

    public ItemTouchAdapter(Context context, Integer[] dataArray) {
        super(context, dataArray);
        mObjects = dataArray;
    }

    @Override
    protected XBaseHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_wheel_layout);
    }

//    @Override
//    public int getCount() {
//        return Integer.MAX_VALUE;
//    }
//
//    @Override
//    public Integer getItemData(int position) {
//        return mObjects[getCount() % mObjects.length];
//    }

    @Override
    public void onItemMove(int from, int to) {
        relocationItem(new ArrayList<>(Arrays.asList(mObjects)), from, to);
        notifyItemMoved(from, to);
    }

    private <T> void relocationItem(List<T> source, int from, int to) {
        ListUtils.relocation(source, from, to);
    }

    static class ViewHolder extends XBaseHolder<Integer> {

        ImageView mImageView;

        ViewHolder(ViewGroup parent, int res) {
            super(parent, res);
            mImageView = getView(R.id.image_view);
        }

        @Override
        protected void setData(Integer resId) {
            mImageView.setImageResource(resId);
        }
    }
}
