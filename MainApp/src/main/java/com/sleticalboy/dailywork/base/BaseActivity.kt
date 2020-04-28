package com.sleticalboy.dailywork.base

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Created on 18-2-1.
 *
 * @author leebin
 * @version 1.0
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val requestCode = 0x12;

    protected var rxPerm: RxPermissions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxPerm = RxPermissions(this)
        prepareWork(savedInstanceState)
        setContentView(layoutResId())
        initView()
        initData()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = requiredPermissions()
            if (perms.isEmpty()) {
                return
            }
            requestPermissions(perms, requestCode)
        }
    }

    protected open fun requiredPermissions(): Array<String> {
        return arrayOf()
    }

    protected abstract fun layoutResId(): Int

    protected abstract fun initView()

    protected open fun initData() {}

    protected open fun prepareWork(savedInstanceState: Bundle?) {}

}
