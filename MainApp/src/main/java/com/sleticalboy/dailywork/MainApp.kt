package com.sleticalboy.dailywork

import android.app.Application
import android.content.Context
import java.lang.ref.Reference
import java.lang.ref.WeakReference

/**
 * Created on 18-3-5.
 *
 * @author leebin
 * @version 1.0
 */
class MainApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        mRefApp = WeakReference(base)
    }

    companion object {

        private var mRefApp: Reference<Context>? = null

        val app: Context?
            get() = mRefApp!!.get()
    }
}
