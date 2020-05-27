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

    private var service: Intent? = null
    private var mService: LocalService? = null
    private val connection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            tvBindProgress.text = "Not bonded..."
            mService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            tvBindProgress.text = "Bonded..."
            mService = (service as LocalService.LocalBinder).service
        }
    }

    override fun layoutResId(): Int = R.layout.activity_service

    override fun initView() {
        btnStart.setOnClickListener {
            startService(getService())
        }
        btnStop.setOnClickListener {
            stopService(getService())
        }

        tvBindProgress.text = "Idle..."
        btnBind.setOnClickListener {
            tvBindProgress.text = "Binding..."
            doBindService()
        }
        btnUnbind.setOnClickListener {
            tvBindProgress.text = "Unbinding..."
            doUnbindService()
        }
    }

    private fun getService(): Intent? {
        if (service == null) {
            service = Intent(applicationContext, MyService::class.java)
        }
        return service
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(getService())
    }

    private fun doBindService() {
        val service = Intent(this, LocalService::class.java)
        bindService(service, connection, Context.BIND_AUTO_CREATE)
    }

    private fun doUnbindService() {
        unbindService(connection)
    }
}
