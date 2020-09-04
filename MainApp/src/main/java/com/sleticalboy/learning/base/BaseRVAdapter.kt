package com.sleticalboy.learning.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*


/**
 * Created on 20-8-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
abstract class BaseRVAdapter<DATA> @JvmOverloads constructor(data: List<DATA>? = ArrayList()) : RecyclerView.Adapter<BaseRVHolder<DATA>>() {
    protected val mData: MutableList<DATA>

    constructor(data: Array<DATA>?) : this(if (data == null) ArrayList<DATA>() else ArrayList<DATA>(Arrays.asList(*data)))

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRVHolder<DATA>
    override fun onBindViewHolder(holder: BaseRVHolder<*>, position: Int) {
        holder.bindData(getData(position), position)
    }

    private fun getData(position: Int): DATA {
        require(position < mData.size) { "position >= mData.size(): $position" }
        return mData[position]
    }

    fun addData(data: DATA): Int {
        val index = mData.indexOf(data)
        if (index < 0) {
            mData.add(data)
        } else {
            mData[index] = data
        }
        val position = if (index < 0) mData.size - 1 else index
        notifyItemChanged(position)
        // Log.d(logTag(), "onDeviceScanned() index: $index, device: $device");
        return position
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    init {
        mData = data ?: ArrayList<DATA>()
    }
}