package com.sleticalboy.learning.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity
import com.sleticalboy.learning.components.service.LocalService
import kotlinx.android.synthetic.main.activity_service.*

class ServicePractise : BaseActivity() {

    private var mService: LocalService? = null

    private val connection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            tvBindProgress.text = "Disconnected"
            mService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            tvBindProgress.text = "Connected"
            mService = (service as LocalService.LocalBinder).getService()
        }
    }

    override fun layoutResId(): Int = R.layout.activity_service

    override fun initView() {
        btnStart.setOnClickListener {
            Log.d(logTag(), "start service")
        }
        btnStop.setOnClickListener {
            Log.d(logTag(), "stop service")
        }

        tvBindProgress.text = "Idle"
        btnBind.setOnClickListener {
            tvBindProgress.text = "Connecting..."
            doBindService()
        }
        btnUnbind.setOnClickListener {
            tvBindProgress.text = "Disconnecting..."
            doUnbindService()
        }
        serviceFoo.setOnClickListener {
        }
    }

    private fun doBindService() {
        val service = Intent(this, LocalService::class.java)
        bindService(service, connection, Context.BIND_AUTO_CREATE)
    }

    private fun doUnbindService() {
        unbindService(connection)
    }
}
