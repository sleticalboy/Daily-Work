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

        val transaction = supportFragmentManager.beginTransaction()
        btCommon.setOnClickListener {
            // open common fragment
            val tag = CommonBtFragment::javaClass.name
            val f = supportFragmentManager.findFragmentByTag(tag)
            if (f != null) {
                transaction.show(f)
            } else {
                transaction.add(R.id.fragmentRoot, CommonBtFragment(), tag)
            }
        }
        ble.setOnClickListener {
            // open ble fragment
            val tag = BleFragment::javaClass.name
            val f = supportFragmentManager.findFragmentByTag(tag)
            if (f == null) {
                transaction.add(R.id.fragmentRoot, BleFragment(), tag)
            } else {
                transaction.show(f)
            }
        }
        transaction.commit()
    }
}