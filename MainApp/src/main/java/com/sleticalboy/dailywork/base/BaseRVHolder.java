package com.sleticalboy.dailywork.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 20-8-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
public abstract class BaseRVHolder<DATA> extends RecyclerView.ViewHolder {

    public BaseRVHolder(@NonNull final View itemView) {
        super(itemView);
    }

    public abstract void bindData(@NonNull DATA data, int position);
}
