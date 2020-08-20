package com.sleticalboy.dailywork.bt

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseFragment
import com.sleticalboy.dailywork.bt.common.BtScanner
import kotlinx.android.synthetic.main.fragment_bt_common.*

/**
 * Created on 20-8-18.
 *
 * @author Ben binli@grandstream.cn
 */
class CommonBtFragment : BaseFragment() {

    private var mScanner: BtScanner? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mScanner = BtScanner(context)
    }

    override fun layout(): Int = R.layout.fragment_bt_common

    override fun initView() {
        startScan.setOnClickListener { doStart() }
        stopScan.setOnClickListener { doStop() }
    }

    private fun doStop() {
        mScanner?.stopScan()
    }

    private fun doStart() {
        mScanner?.startScan(object : BtScanner.Callback {
            override fun onDeviceFound(device: BluetoothDevice?, rssi: Int) {
                Log.d(logTag(), "onDeviceFound() called with: device = $device, rssi = $rssi")
            }
        })
    }

    override fun logTag(): String = "CommonBtFragment"
}