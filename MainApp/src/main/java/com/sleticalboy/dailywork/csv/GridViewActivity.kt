package com.sleticalboy.dailywork.csv

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import kotlinx.android.synthetic.main.activity_grid_view.*

/**
 * Created on 20-9-1.
 *
 * @author binlee sleticalboy@gmail.com
 */
class GridViewActivity : BaseActivity() {

    private val mData = arrayOf(
            "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6",
            "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12",
            "Item 13", "Item 14", "Item 15", "Item 16", "Item 17", "Item 18"
    )

    override fun layoutResId(): Int = R.layout.activity_grid_view

    override fun initView() {
        gridView.adapter = MyAdapter()
    }

    override fun logTag(): String = "GridViewActivity"

    inner class MyAdapter : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemView: View
            val holder: ItemHolder
            if (convertView == null) {
                itemView = layoutInflater.inflate(R.layout.item_grid_view, parent, false)
                holder = ItemHolder(itemView)
            } else {
                itemView = convertView
                holder = convertView.tag as ItemHolder
            }
            Log.d(logTag(), "getView() position = $position, data = ${mData[position]}")
            holder.mTitle.text = mData[position]
            holder.mSummary.text = mData[position]
            itemView.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event?.action == KeyEvent.ACTION_UP) {
                        gridView.smoothScrollToPosition(position + 1)
                        return true
                    }
                    return false
                }
            })
            return itemView
        }

        override fun getItem(position: Int): String = mData[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = mData.size
    }

    class ItemHolder(itemView: View) {

        init {
            itemView.tag = this
        }

        val mTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val mSummary: TextView = itemView.findViewById(R.id.tvSummary)
    }
}