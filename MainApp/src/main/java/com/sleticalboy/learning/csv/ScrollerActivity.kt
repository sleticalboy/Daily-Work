package com.sleticalboy.learning.csv

import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity


/**
 * Created on 18-6-10.
 *
 * @author leebin
 * @description Scroller 使用练习
 */
class ScrollerActivity : BaseActivity() {
    private var layout: LinearLayout? = null
    override fun layoutResId(): Int {
        return R.layout.activity_scroller
    }

    override fun initView() {
        layout = findViewById(R.id.layout)
        val scrollToBtn = findViewById<Button>(R.id.scroll_to_btn)
        val scrollByBtn = findViewById<Button>(R.id.scroll_by_btn)
        scrollToBtn.setOnClickListener { v: View? -> layout.scrollTo(-60, -100) }
        scrollByBtn.setOnClickListener { v: View? -> layout.scrollBy(-60, -100) }
    }

    override fun initData() {}
}