package com.sleticalboy.learning.bt.common

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log


/**
 * Created on 20-8-17.
 *
 * @author Ben binli@grandstream.cn
 */
class BtScanner(context: Context) {
    private val mContext: Context
    private var mCallback: Callback? = null
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
                        == BluetoothAdapter.STATE_ON) {
                    startScan(mCallback)
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                // discovery started
                Log.d(TAG, "receive action: $action, discovery started")
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                // discovery finished
                Log.d(TAG, "receive action: $action, discovery finished")
            } else if (BluetoothDevice.ACTION_FOUND == action) {
                // bt device found
                if (mCallback != null) {
                    mCallback!!.onDeviceFound(getDevice(intent),
                            intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, 0.toShort())
                                    .toInt())
                }
            }
        }

        private fun getDevice(intent: Intent): BluetoothDevice {
            return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }
    }

    fun startScan(callback: Callback?): Boolean {
        requireNotNull(callback) { "callback is null." }
        mCallback = callback
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter.state == BluetoothAdapter.STATE_ON) {
            return adapter.startDiscovery()
        } else {
            adapter.enable()
        }
        return false
    }

    fun stopScan(): Boolean {
        return BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
    }

    fun destroy() {
        stopScan()
        mCallback = null
        mContext.unregisterReceiver(mReceiver)
    }

    interface Callback {
        fun onDeviceFound(device: BluetoothDevice?, rssi: Int)
    }

    companion object {
        private const val TAG = "BtScanner"
    }

    init {
        val filters = IntentFilter()
        filters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filters.addAction(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(mReceiver, filters)
        mContext = context.applicationContext
    }
}