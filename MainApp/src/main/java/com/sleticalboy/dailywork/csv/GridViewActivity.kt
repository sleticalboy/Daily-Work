package com.sleticalboy.dailywork.csv

import android.util.Log
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.dailywork.bean.AudioItem
import kotlinx.android.synthetic.main.activity_grid_view.*

/**
 * Created on 20-9-1.
 *
 * @author binlee sleticalboy@gmail.com
 */
class GridViewActivity : BaseActivity() {

    private var mData: ArrayList<AudioItem>? = null
    private var mSelection: Int = -1

    override fun initData() {
        if (mSelection == -1) {
            mSelection = intent.getIntExtra("mock_data", 26)
        }
        if (mData == null) {
            mData = ArrayList()
            var item: AudioItem
            for (index in 0..42) {
                item = AudioItem()
                item.mTitle = "Item ${index + 1}"
                item.mSummary = "Item ${index + 1}"
                if (mSelection == index) {
                    item.mColor = android.R.color.holo_red_light
                    item.mColor2 = android.R.color.holo_blue_light
                } else {
                    item.mColor = android.R.color.black
                    item.mColor2 = android.R.color.black
                }
                mData!!.add(item)
            }
        }
        Log.d(logTag(), "initData() selection: $mSelection, data size: ${mData!!.size}")
    }

    override fun layoutResId(): Int = R.layout.activity_grid_view

    override fun logTag(): String = "GridViewActivity"

    override fun initView() {
        btnShowDialog.setOnClickListener {
            showGridDialog()
        }
    }

    private fun showGridDialog() {
        val dialog = GridDialog()
        dialog.setSelection(mSelection)
        dialog.setData(mData!!)
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }
}