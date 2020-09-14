package com.sleticalboy.learning.anims

import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity
import kotlinx.android.synthetic.main.activity_json_anim.*

/**
 * Created on 20-9-9.
 *
 * @author Ben binli@grandstream.cn
 */
class JsonAnimUI : BaseActivity() {

    override fun layoutResId(): Int = R.layout.activity_json_anim

    override fun initView() {
        lottieAnimView
    }
}