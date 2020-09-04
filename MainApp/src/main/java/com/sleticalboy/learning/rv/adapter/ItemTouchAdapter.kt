package com.sleticalboy.learning.rv.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.sleticalboy.learning.R
import com.sleticalboy.util.ListUtils
import com.sleticalboy.weight.xrecycler.adapter.XBaseHolder
import com.sleticalboy.weight.xrecycler.adapter.XRecyclerAdapter
import com.sleticalboy.weight.xrecycler.helper.ItemTouchDragAdapter
import java.util.*

/**
 * Created on 18-2-7.
 *
 * @author leebin
 * @version 1.0
 * @description
 */
class ItemTouchAdapter(context: Context, private val mObjects: Array<Int>)
    : XRecyclerAdapter<Int>(context, mObjects), ItemTouchDragAdapter {
    override fun onCreateItemHolder(parent: ViewGroup, viewType: Int): XBaseHolder<*> {
        return ViewHolder(parent, R.layout.item_wheel_layout)
    }

    override fun onItemMove(from: Int, to: Int) {
        relocationItem(ArrayList(listOf(*mObjects)), from, to)
        notifyItemMoved(from, to)
    }

    private fun <T> relocationItem(source: List<T>, from: Int, to: Int) {
        ListUtils.relocation(source.toMutableList(), from, to)
    }

    internal class ViewHolder(parent: ViewGroup?, res: Int) : XBaseHolder<Int?>(parent!!, res) {

        private var mImageView: ImageView = getView(R.id.image_view)

        protected override fun bindData(resId: Int?) {
            resId?.let { mImageView.setImageResource(it) }
        }

    }
}