package com.sleticalboy.dailywork.weight.xrecycler.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public abstract class XRecyclerAdapter<M> extends RecyclerView.Adapter<XBaseHolder> {

    private final Object mLock = new Object();

    protected List<HeaderView> mHeaders = new ArrayList<>();
    protected List<FooterView> mFooters = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    protected List<M> mDataList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private boolean mNotifyOnChange = false;
    private EventDelegate mEventDelegate;

    public XRecyclerAdapter(Context context) {
        init(context, new ArrayList<M>());
    }

    public XRecyclerAdapter(Context context, M[] dataArray) {
        init(context, Arrays.asList(dataArray));
    }

    public XRecyclerAdapter(Context context, List<M> dataList) {
        init(context, dataList);
    }

    private void init(Context context, List<M> dataList) {
        mContext = context;
        mDataList = new ArrayList<>(dataList);
    }

    // placeholder
    private static class PlaceHolder extends XBaseHolder {
        PlaceHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setData(Object data) {
        }
    }

    @Override
    public final XBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = createViewByViewType(parent, viewType);
        if (view != null) {
            return new PlaceHolder(view);
        }
        final XBaseHolder holder = onCreateItemHolder(parent, viewType);
        final int position = holder.getAdapterPosition() - mHeaders.size();
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnItemLongClickListener.onItemLongClick(position);
                }
            });
        }
        return holder;
    }

    protected abstract XBaseHolder onCreateItemHolder(ViewGroup parent, int viewType);

    private View createViewByViewType(ViewGroup parent, int viewType) {
        for (HeaderView headerView : mHeaders) {
            if (headerView.hashCode() == viewType) {
                return getView(parent, headerView);
            }
        }
        for (FooterView footerView : mFooters) {
            if (footerView.hashCode() == viewType) {
                return getView(parent, footerView);
            }
        }
        return null;
    }

    @NonNull
    private View getView(ViewGroup parent, ItemView itemView) {
        View view = itemView.onCreateView(parent);
        StaggeredGridLayoutManager.LayoutParams params;
        if (view.getLayoutParams() != null)
            params = new StaggeredGridLayoutManager.LayoutParams(view.getLayoutParams());
        else
            params = new StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setFullSpan(true);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public final void onBindViewHolder(XBaseHolder holder, int position) {
        holder.itemView.setId(position);
        if (mHeaders.size() != 0 && position < mHeaders.size()) {
            mHeaders.get(position).onBindView(holder.itemView);
            return;
        }
        if (mFooters.size() != 0 && position >= mHeaders.size() + mDataList.size()) {
            mFooters.get(position - mHeaders.size() - mDataList.size()).onBindView(holder.itemView);
        }
        onBindItemHolder(holder, position);
    }

    protected void onBindItemHolder(XBaseHolder holder, int position) {
        holder.setData(getItemData(position));
    }

    /**
     * 获取 item 数据
     *
     * @param position
     * @return
     */
    public M getItemData(int position) {
        return mDataList.get(position);
    }

    @Override
    public final int getItemViewType(int position) {
        if (mHeaders.size() != 0) {
            if (position < mHeaders.size()) {
                return mHeaders.get(position).hashCode();
            }
        }
        if (mFooters.size() != 0) {
            if (position >= mHeaders.size() + mDataList.size()) {
                return mFooters.get(position - mHeaders.size() - mDataList.size()).hashCode();
            }
        }
        return getViewType(position - mHeaders.size());
    }

    private int getViewType(int position) {
        return 0;
    }

    @Override
    public final int getItemCount() {
        return mHeaders.size() + getCount() + mFooters.size();
    }

    public void setData(List<M> dataList) {
        mDataList = dataList;
    }

    public void setData(M[] dataArray) {
        mDataList = new ArrayList<>(Arrays.asList(dataArray));
    }

    public List<M> getAllData() {
        return mDataList;
    }

    public int getCount() {
        return mDataList.size();
    }

    public int getPosition(M item) {
        return mDataList.indexOf(item);
    }

    // --------------------对数据的一些操作-------------------

    public void add(M object) {
        add(getCount(), object);
    }

    public void add(int index, M object) {
        if (index > getCount() || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + getCount());
        }
        if (object != null) {
            synchronized (mLock) {
                mDataList.add(index, object);
            }
        }
        if (mNotifyOnChange) {
            notifyItemInserted(index);
        }
    }

    public void addAll(Collection<? extends M> collection) {
        addAll(getCount(), collection);
    }

    public void addAll(int index, Collection<? extends M> collection) {
        if (index > getCount() || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + getCount());
        }
        if (collection != null && collection.size() != 0) {
            synchronized (mLock) {
                mDataList.addAll(index, collection);
            }
        }
        int dataCount = collection == null ? 0 : collection.size();
        if (mNotifyOnChange) {
            notifyItemRangeInserted(index, dataCount);
        }
    }

    public void update(int position, M object) {
        synchronized (mLock) {
            mDataList.set(position, object);
        }
        if (mNotifyOnChange) {
            notifyItemChanged(position);
        }
    }

    public void remove(M object) {
        int position = mDataList.indexOf(object);
        synchronized (mLock) {
            if (mDataList.remove(object)) {
                if (mNotifyOnChange) {
                    notifyItemRemoved(position);
                }
            }
        }
    }

    public void remove(int position) {
        synchronized (mLock) {
            mDataList.remove(position);
        }
        if (mNotifyOnChange) {
            notifyItemRemoved(position);
        }
    }

    public void removeAll() {
        int count = mDataList.size();
        synchronized (mLock) {
            mDataList.clear();
        }
        if (mNotifyOnChange) {
            notifyItemRangeChanged(0, count);
        }
    }

    public void clear() {
        int count = mDataList.size();
        synchronized (mLock) {
            mDataList.clear();
        }
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    public void sort(Comparator<? super M> comparator) {
        synchronized (mLock) {
            Collections.sort(mDataList, comparator);
        }
        if (mNotifyOnChange) {
            notifyDataSetChanged();
        }
    }

    // --------------------对数据的一些操作-------------------

    // -------------对 header 和 footer 的一些操作------------

    public void addHeader(HeaderView headerView) {
        if (headerView == null) {
            throw new NullPointerException("view can not be null");
        }
        mHeaders.add(headerView);
        notifyItemInserted(mHeaders.size() - 1);
    }

    public HeaderView getHeader(int position) {
        return mHeaders.get(position);
    }

    public void removeHeader(HeaderView headerView) {
        int position = mHeaders.indexOf(headerView);
        mHeaders.remove(headerView);
        notifyItemRemoved(position);
    }

    public void removeAllHeaders() {
        int count = mHeaders.size();
        mHeaders.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void addFooter(FooterView footerView) {
        if (footerView == null) {
            throw new NullPointerException("view can not be null");
        }
        mFooters.add(footerView);
        notifyItemInserted(mFooters.size() - 1);
    }

    public FooterView getFooter(int position) {
        return mFooters.get(position);
    }

    public void removeFooter(FooterView footerView) {
        int position = mFooters.indexOf(footerView);
        mFooters.remove(footerView);
        notifyItemRemoved(position);
    }

    public void removeAllFooters() {
        int count = mFooters.size();
        mFooters.clear();
        notifyItemRangeRemoved(0, count);
    }

    public int getHeadersCount() {
        return mHeaders.size();
    }

    public int getFootersCount() {
        return mFooters.size();
    }

    // -------------对 header 和 footer 的一些操作------------

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    /**
     * Header or Footer
     */
    private interface ItemView {

        /**
         * Call when the item was created
         *
         * @return the View itself
         */
        View onCreateView(ViewGroup parent);

        /**
         * Called when  binding view
         *
         * @param itemView item view
         */
        void onBindView(View itemView);
    }

    public interface HeaderView extends ItemView {
    }

    public interface FooterView extends ItemView {
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(int position);
    }

    public EventDelegate getEventDelegate() {
        if (mEventDelegate == null) {
            mEventDelegate = new EventDelegateImpl(this);
        }
        return mEventDelegate;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }
}
