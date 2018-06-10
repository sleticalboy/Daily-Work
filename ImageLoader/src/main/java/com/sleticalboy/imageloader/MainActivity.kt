package com.sleticalboy.imageloader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val image = ImageView(this)
        image.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        image.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        val imageLoader = ImageLoader.getInstance()
        val configuration = ImageLoaderConfiguration.createDefault(applicationContext)
        imageLoader.init(configuration)
        imageLoader.displayImage("", image)
        imageLoader.pause()
        imageLoader.resume()
        imageLoader.stop()
        imageLoader.destroy()
        imageLoader.clearDiscCache()
        imageLoader.clearDiskCache()
        imageLoader.clearMemoryCache()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }
}
