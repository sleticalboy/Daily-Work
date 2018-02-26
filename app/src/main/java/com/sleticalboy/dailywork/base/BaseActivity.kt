package com.sleticalboy.dailywork.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View

/**
 * Created on 18-2-1.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
abstract class BaseActivity : PermissionCheckActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareWork()
        setContentView(attachLayout())
        initView()
        initData()
    }

    protected abstract fun initData()

    protected abstract fun initView()

    protected abstract fun attachLayout(): Int

    protected fun prepareWork() {}

}
