package com.sleticalboy.dailywork.weight.xrecycler.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public abstract class AbsItemTouchCallback extends ItemTouchHelper.Callback {

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ItemTouchHelperViewHolder) {
            ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
        }
        super.clearView(recyclerView, viewHolder);
    }
}
