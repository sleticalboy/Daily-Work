package com.sleticalboy.learning.csv

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity


/**
 * Created on 18-5-29.
 *
 * @author leebin
 * @description 控件透明度研究
 */
class AlphaActivity : BaseActivity() {

    private var tvShow: TextView? = null
    private var seekBar: SeekBar? = null
    private var touchButton: Button? = null
    private val mLocation = IntArray(2)

    override fun layoutResId(): Int {
        return R.layout.activity_alpha
    }

    override fun initView() {
        tvShow = findViewById(R.id.tv_show)
        seekBar = findViewById(R.id.seekBar)
        touchButton = findViewById(R.id.touch_button)
        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "onProgressChanged() progress = [$progress], fromUser = [$fromUser]")
                tvShow?.alpha = progress / 100f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.d(TAG, "onStartTrackingTouch() called with: seekBar = [$seekBar]")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d(TAG, "onStopTrackingTouch() called with: seekBar = [$seekBar]")
            }
        })
        touchButton?.setOnTouchListener(OnTouchListener { _: View?, _: MotionEvent? ->
            touchButton?.getLocationInWindow(mLocation)
            Log.d(TAG, "mLocation[0]:" + mLocation[0])
            Log.d(TAG, "mLocation[1]:" + mLocation[1])
            Log.d(TAG, "touchButton.getMeasuredWidth():" + touchButton?.measuredWidth)
            Log.d(TAG, "touchButton.getMeasuredHeight():" + touchButton?.measuredHeight)
            false
        })
    }

    companion object {
        private const val TAG = "AlphaActivity"
    }
}