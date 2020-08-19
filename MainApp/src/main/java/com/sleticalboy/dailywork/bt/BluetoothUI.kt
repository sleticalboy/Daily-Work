package com.sleticalboy.dailywork.bt

import android.Manifest
import android.util.Log
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bluetooth_main.*

class BluetoothUI : BaseActivity() {

    override fun logTag(): String = "BluetoothUI"

    override fun layoutResId(): Int = R.layout.activity_bluetooth_main

    override fun requiredPermissions(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCode) {
            Log.d(logTag(), "onRequestPermissionsResult() permission granted.")
        }
    }

    override fun initView() {
        btCommon.setOnClickListener {
            // open common fragment
            showFragment(CommonBtFragment::class.java.name)
        }
        ble.setOnClickListener {
            // open ble fragment
            showFragment(BleFragment::class.java.name)
        }
    }

    private fun showFragment(tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        var f = supportFragmentManager.findFragmentByTag(tag)
        if (f == null) {
            f = supportFragmentManager.fragmentFactory.instantiate(classLoader, tag)
            transaction.replace(R.id.fragmentRoot, f, tag)
        } else {
            if (f.isAdded) {
                transaction.show(f)
            }
        }
        transaction.commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }
}