package com.sleticalboy.learning.camera.v1

import android.Manifest
import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity
import com.sleticalboy.learning.camera.CameraCompat

/**
 * Created on 18-2-26.
 *
 * @author leebin
 * @version 1.0
 * @description
 */
class NewCameraActivity : BaseActivity() {

    private lateinit var mCameraCompat: CameraCompat

    override fun initData() {
        mCameraCompat = CameraCompat(this)
    }

    override fun initView() {}

    override fun layoutResId(): Int {
        return R.layout.activity_empty
    }

    override fun logTag(): String = "NewCamera"

    override fun requiredPermissions(): Array<String> {
        return arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == super.requestCode) {
            mCameraCompat.openCamera()
        }
    }
}