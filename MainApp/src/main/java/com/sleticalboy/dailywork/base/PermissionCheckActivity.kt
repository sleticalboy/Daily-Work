package com.sleticalboy.dailywork.base

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

/**
 * Created on 18-1-31.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
open class PermissionCheckActivity : AppCompatActivity() {

    /**
     * 必须要动态授权的权限, 每个权限代表一个权限组, 只要授权一个, 该组的权限都可以使用
     */
    private val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != 0 ||
                ContextCompat.checkSelfPermission(this, permissions[1]) != 0 ||
                ContextCompat.checkSelfPermission(this, permissions[2]) != 0) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty()) {
            grantResults.indices
                    .filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
                    .forEach { Log.d("PermissionCheckActivity", permissions[it] + " 没有授权") }
        }
    }

    companion object {
        protected val REQUEST_CODE = 1000
    }
}
