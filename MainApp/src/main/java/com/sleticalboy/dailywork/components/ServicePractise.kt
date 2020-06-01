package com.sleticalboy.dailywork.components

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.dailywork.components.service.LocalService
import com.sleticalboy.dailywork.components.service.MyService
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
            mService = (service as LocalService.LocalBinder).service
        }
    }

    override fun layoutResId(): Int = R.layout.activity_service

    override fun initView() {
        btnStart.setOnClickListener {
            startService(Intent(this, MyService::class.java))
        }
        btnStop.setOnClickListener {
            stopService(Intent(this, MyService::class.java))
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
            mService?.foo { tvBindProgress.text = "Unbind called" }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MyService::class.java))
    }

    private fun doBindService() {
        val service = Intent(this, LocalService::class.java)
        bindService(service, connection, Context.BIND_AUTO_CREATE)
    }

    private fun doUnbindService() {
        unbindService(connection)
    }
}
