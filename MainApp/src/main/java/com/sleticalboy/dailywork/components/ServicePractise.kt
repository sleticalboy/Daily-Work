package com.sleticalboy.dailywork.components

import android.content.Intent
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import kotlinx.android.synthetic.main.activity_service.*

class ServicePractise : BaseActivity() {

    private var service: Intent? = null

    override fun layoutResId(): Int = R.layout.activity_service

    override fun initView() {
        btnStart.setOnClickListener {
            startService(getService())
        }
        btnStop.setOnClickListener {
            stopService(getService())
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
}
