package com.sleticalboy.learning.csv

import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity
import com.sleticalboy.weight.AutoSwitchView
import java.util.*


/**
 * Created on 18-10-23.
 *
 * @author leebin
 */
class AutoSwitchUI : BaseActivity() {
    private var autoSwitchView: AutoSwitchView? = null
    override fun layoutResId(): Int {
        return R.layout.activity_auto_switch
    }

    override fun initView() {
        autoSwitchView = findViewById(R.id.auto_switch_view)
    }

    override fun initData() {
        val textList: MutableList<String?> = ArrayList()
        for (i in 0..30) {
            textList.add("第 $i 条数据")
        }
        autoSwitchView!!.setTextList(textList)
        autoSwitchView!!.setAnimTime(300)
        autoSwitchView!!.setTextStillTime(3000)
    }

    override fun onPause() {
        super.onPause()
        autoSwitchView!!.stop()
    }

    override fun onResume() {
        super.onResume()
        autoSwitchView!!.start()
    }
}