package com.binlee.emoji

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.emoji_panel -> {
                return true
            }
            R.id.http_service -> {
                return true
            }
            R.id.remote_upgrade -> {
                requestUpgrade()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestUpgrade() {
        val service = Intent()
        // Service 不支持隐式 Intent 启动, 需要设置具体的包名和类名才可以
        service.action = "com.sleticalboy.action.REMOTE_UPGRADE"
        // com.sleticalboy.learning.components.service.UpgradeService
        service.setClassName("com.sleticalboy.learning", "com.sleticalboy.learning.components.service.UpgradeService")
        service.putExtra("_mac", "fake mac address")
        service.putExtra("_file_url", "fake file url")
        val component = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(service)
        } else {
            startService(service)
        }
        Log.d(TAG, "requestUpgrade() $component")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
