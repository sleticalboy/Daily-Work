package com.sleticalboy.dailywork.bt

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseFragment
import com.sleticalboy.dailywork.bt.common.BtScanner
import kotlinx.android.synthetic.main.fragment_bt_common.*
import java.util.*

/**
 * Created on 20-8-18.
 *
 * @author Ben binli@grandstream.cn
 */
class CommonBtFragment : BaseFragment() {

    private var mScanner: BtScanner? = null
    private var mAdapter: DevicesAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mScanner = BtScanner(context)
    }

    override fun layout(): Int = R.layout.fragment_bt_common

    override fun logTag(): String = "CommonBtFragment"

    override fun initView() {
        startScan.setOnClickListener { doStart() }
        stopScan.setOnClickListener { doStop() }

        btDevicesRv.layoutManager = LinearLayoutManager(context)
        btDevicesRv.adapter = DevicesAdapter().also { mAdapter = it }
    }

    private fun doStop() {
        mScanner?.stopScan()
    }

    private fun doStart() {
        mScanner?.startScan { device, rssi ->
            Log.d(logTag(), "onDeviceFound() called with: device = $device, rssi = $rssi")
            mAdapter?.addDevice(device)
        }
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

    private inner class DevicesAdapter : RecyclerView.Adapter<DeviceHolder>() {

        private val mDataSet: MutableList<BluetoothDevice> = ArrayList()
        private val mDataCopy: MutableList<BluetoothDevice> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            return DeviceHolder(layoutInflater.inflate(
                    R.layout.item_ble_recycler, parent, false))
        }

        override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
            val device = mDataSet[position]
            holder.tvName.text = Html.fromHtml("<font color='red'>" + device.name
                    + "</font>  <font color='blue'>" + device.address)
            holder.btnConnect.isEnabled = true
            holder.btnConnect.setOnClickListener {
                doConnect(device)
            }
        }

        override fun getItemCount(): Int {
            return mDataSet.size
        }

        val data: List<BluetoothDevice>
            get() {
                mDataCopy.clear()
                mDataCopy.addAll(mDataSet)
                return mDataCopy
            }

        fun addDevice(device: BluetoothDevice) {
            val index = mDataSet.indexOf(device)
            if (index < 0) {
                mDataSet.add(device)
            } else {
                mDataSet[index] = device
            }
            Log.d(logTag(), "onDeviceScanned() index: $index, device: $device")
            notifyItemChanged(if (index < 0) mDataSet.size - 1 else index)
        }
    }

    private class DeviceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_ble_name)
        val btnConnect: TextView = itemView.findViewById(R.id.btn_connect)
    }
}