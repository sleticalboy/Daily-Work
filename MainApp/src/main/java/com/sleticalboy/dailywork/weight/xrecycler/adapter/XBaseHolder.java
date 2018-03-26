package com.sleticalboy.dailywork.weight.xrecycler.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public abstract class XBaseHolder<M> extends RecyclerView.ViewHolder {

    public XBaseHolder(View itemView) {
        super(itemView);
    }

    public XBaseHolder(ViewGroup parent, @LayoutRes int res) {
        this(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
    }

    protected <V extends View> V getView(@IdRes int id) {
        return (V) itemView.findViewById(id);
    }

    protected int getDataPosition() {
        RecyclerView.Adapter adapter = getOwnerAdapter();
        if (adapter != null && adapter instanceof XRecyclerAdapter) {
            return getAdapterPosition() - ((XRecyclerAdapter) adapter).getHeadersCount();
        }
        return getAdapterPosition();
    }

    @Nullable
    protected <A extends RecyclerView.Adapter> A getOwnerAdapter() {
        RecyclerView recyclerView = getOwnerRecyclerView();
        return recyclerView == null ? null : (A) recyclerView.getAdapter();
    }

    protected RecyclerView getOwnerRecyclerView() {
        try {
            Field mOwnerRecyclerView = RecyclerView.ViewHolder.class.getDeclaredField("mOwnerRecyclerView");
            mOwnerRecyclerView.setAccessible(true);
            return (RecyclerView) mOwnerRecyclerView.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    protected abstract void setData(M data);
}
