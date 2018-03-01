package com.sleticalboy.dailywork.ui.activity

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

/**
 * Created on 18-1-29.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 启动界面
 */
class StartActivity : ListActivity() {

    private val dataList = arrayOf(
            ItemHolder(LiveCameraActivity::class.java, "TextureView 实现实时预览"),
            ItemHolder(FaceActivity::class.java, "SurfaceView 实现实时预览"),
            ItemHolder(NewCameraActivity::class.java, "camera2 API 测试"),
            ItemHolder(ClassifyActivity::class.java, "RecyclerView 分类别显示"),
            ItemHolder(PagerActivity::class.java, "RecyclerView 分页"),
            ItemHolder(DecorationActivity::class.java, "RecyclerView 添加 item 分割线 / 拖拽排序"),
            ItemHolder(WheelRVActivity::class.java, "RecyclerView 轮播"),
            ItemHolder(PullRefreshActivity::class.java, "下拉刷新库测试"),
            ItemHolder(RefreshActivity::class.java, "下拉刷新 View"),
            ItemHolder(SmsSenderActivity::class.java, "加密短信发送"),
            ItemHolder(StockActivity::class.java, "股票 View")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, dataList)
        setListAdapter(listAdapter)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val holder = dataList[position]
        (v as TextView).text = holder.mName
        startActivity(Intent(this, holder.mClass))
    }

    class ItemHolder(internal var mClass: Class<*>, internal var mName: String) {

        override fun toString(): String {
            return mName
        }
    }
}
