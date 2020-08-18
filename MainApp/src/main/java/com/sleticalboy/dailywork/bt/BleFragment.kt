package com.sleticalboy.dailywork.bt

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseFragment
import com.sleticalboy.dailywork.bt.ble.BleScanner
import com.sleticalboy.dailywork.bt.ble.BleService
import com.sleticalboy.dailywork.bt.ble.Connection
import com.sleticalboy.dailywork.bt.ble.IConnectCallback
import kotlinx.android.synthetic.main.fragment_bt_ble.*
import java.util.*

/**
 * Created on 20-8-18.
 *
 * @author Ben binli@grandstream.cn
 */
class BleFragment : BaseFragment() {

    private var mService: BleService.LeBinder? = null
    private var mAdapter: DevicesAdapter? = null
    private val mConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(logTag(), "onServiceConnected() name: $name, service: $service")
            mService = service as BleService.LeBinder
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(logTag(), "onServiceDisconnected() name:$name")
            mService = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val intent = Intent(context, BleService::class.java)
        context.bindService(intent, mConn, Context.BIND_AUTO_CREATE)
    }

    override fun layout(): Int = R.layout.fragment_bt_ble

    override fun initView() {
        startScan.setOnClickListener { startBtScan() }
        stopScan.setOnClickListener { stopBtScan() }
        btDevicesRv.layoutManager = LinearLayoutManager(context)
        btDevicesRv.adapter = DevicesAdapter().also { mAdapter = it }
    }

    private fun startBtScan() {
        if (mService == null) {
            return
        }
        val request = BleScanner.Request()
        request.mCallback = object : BleScanner.Callback() {
            override fun onScanResult(result: BleScanner.Result) {
                // Log.d(logTag(), "onDeviceScanned() " + result);
                mAdapter!!.addDevice(result)
            }

            override fun onScanFailed(errorCode: Int) {}
        }
        request.mDuration = 10000L
        mService!!.startScan(request)
    }

    private fun stopBtScan() {
        if (mService != null) {
            mService!!.stopScan()
        }
    }

    private fun doConnect(device: BluetoothDevice) {
        Log.d(logTag(), "connect to $device")
        if (mService != null) {
            mService!!.connect(device, object : IConnectCallback {
                override fun onFailure(connection: Connection) {
                }

                override fun onSuccess(connection: Connection) {
                }
            })
        }
    }

    private fun doCancel(device: BluetoothDevice?) {
        mService?.cancel(device)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopBtScan()
        doCancel(null)
        context?.unbindService(mConn)
    }
    
    override fun logTag(): String = "BleFragment"

    private inner class DevicesAdapter : RecyclerView.Adapter<DeviceHolder>() {
        private val mDataSet: MutableList<BleScanner.Result> = ArrayList()
        private val mDataCopy: MutableList<BleScanner.Result> = ArrayList()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
            return DeviceHolder(layoutInflater.inflate(
                    R.layout.item_ble_recycler, parent, false))
        }

        override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
            val result = mDataSet[position]
            holder.tvName.text = Html.fromHtml("<font color='red'>" + result.mDevice.name
                    + "</font>  <font color='blue'>" + result.mDevice.address)
            holder.btnConnect.isEnabled = result.mConnectable
            holder.btnConnect.setOnClickListener { doConnect(result.mDevice) }
        }

        override fun getItemCount(): Int {
            return mDataSet.size
        }

        val data: List<BleScanner.Result>
            get() {
                mDataCopy.clear()
                mDataCopy.addAll(mDataSet)
                return mDataCopy
            }

        fun addDevice(result: BleScanner.Result) {
            val index = mDataSet.indexOf(result)
            if (index < 0) {
                mDataSet.add(result)
            } else {
                mDataSet[index] = result
            }
            Log.d(logTag(), "onDeviceScanned() index: $index, device: $result")
            notifyItemChanged(if (index < 0) mDataSet.size - 1 else index)
        }
    }

    private class DeviceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_ble_name)
        val btnConnect: TextView = itemView.findViewById(R.id.btn_connect)
    }
}