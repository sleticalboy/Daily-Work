package com.sleticalboy.learning.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created on 20-8-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
public abstract class BaseRVHolder<DATA> extends RecyclerView.ViewHolder {

    public BaseRVHolder(@NonNull final ViewGroup parent, @LayoutRes int layout) {
        super(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    public abstract void bindData(@NonNull DATA data, int position);
}
