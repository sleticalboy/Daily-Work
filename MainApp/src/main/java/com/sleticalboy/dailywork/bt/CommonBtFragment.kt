package com.sleticalboy.dailywork.bt

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseListFragment
import com.sleticalboy.dailywork.base.BaseRVAdapter
import com.sleticalboy.dailywork.base.BaseRVHolder
import com.sleticalboy.dailywork.bt.common.BtScanner
import kotlinx.android.synthetic.main.bt_common_header.*

/**
 * Created on 20-8-18.
 *
 * @author Ben binli@grandstream.cn
 */
class CommonBtFragment : BaseListFragment<BluetoothDevice>() {

    private var mScanner: BtScanner? = null
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val device = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
            val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
            if (state == BluetoothDevice.BOND_BONDED) {
                //
            }
            Log.d(logTag(), "receive action: " + intent.action + ", " + device + ", " + BtUtils.bondStr(state))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mScanner = BtScanner(context)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(mReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
    }

    override fun logTag(): String = "CommonBtFragment"

    override fun initHeader(headerContainer: FrameLayout) {
        layoutInflater.inflate(R.layout.bt_common_header, headerContainer, true)
        startScan.setOnClickListener { doStart() }
        stopScan.setOnClickListener { doStop() }
    }

    override fun createAdapter(): BaseRVAdapter<BluetoothDevice> {
        return DevicesAdapter()
    }

    private fun doStop() {
        mScanner?.stopScan()
    }

    private fun doStart() {
        mScanner?.startScan { device, rssi ->
            val position = getAdapter().addData(device)
            Log.d(logTag(), "onDeviceFound() device: $device, rssi: $rssi, pos: $position")
        }
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(mReceiver)
    }

    override fun onDetach() {
        super.onDetach()
        mScanner?.destroy()
    }

    private fun doConnect(device: BluetoothDevice) {
        when (device.bondState) {
            BluetoothDevice.BOND_BONDED -> {
                Log.d(logTag(), "doConnect() $device bonded")
            }
            BluetoothDevice.BOND_NONE -> {
                Log.d(logTag(), "doConnect() $device will create bond: ${device.createBond()}")
            }
            else -> {
                Log.d(logTag(), "doConnect() $device bonding...")
            }
        }
    }

    private inner class DevicesAdapter : BaseRVAdapter<BluetoothDevice>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            return DeviceHolder(layoutInflater.inflate(
                    R.layout.item_ble_recycler, parent, false))
        }
    }

    private inner class DeviceHolder(itemView: View) : BaseRVHolder<BluetoothDevice>(itemView) {

        val tvName: TextView = itemView.findViewById(R.id.tv_ble_name)
        val btnConnect: TextView = itemView.findViewById(R.id.btn_connect)

        override fun bindData(data: BluetoothDevice, position: Int) {
            tvName.text = Html.fromHtml("<font color='red'>" + data.name
                    + "</font>  <font color='blue'>" + data.address)
            btnConnect.isEnabled = true
            btnConnect.setOnClickListener {
                doConnect(data)
            }
        }
    }
}