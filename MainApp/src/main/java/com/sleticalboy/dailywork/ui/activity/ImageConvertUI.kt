package com.sleticalboy.dailywork.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.dailywork.util.ImageUtils

/**
 * Created on 18-9-26.
 *
 * @author sleticalboy
 */
class ImageConvertUI : BaseActivity() {

    private var imageView: ImageView? = null
    private var tvImageStr: TextView? = null
    private var imageStr: String? = null
    private var resultBitmap: Bitmap? = null

    override fun layoutResId(): Int {
        return R.layout.image_convert_activity
    }

    override fun initView() {
        imageView = findViewById<View>(R.id.image_view) as ImageView
        tvImageStr = findViewById<View>(R.id.tv_image_str) as TextView
    }

    override fun initData() {
        imageStr = ImageUtils.bitmap2StrByBase64(
                BitmapFactory.decodeResource(resources, R.drawable.btn_shutter_default)
        )
        Log.d("ImageConvertUI", imageStr)
        resultBitmap = ImageUtils.base64ToBitmap(imageStr!!)
    }

    fun imageToString(view: View) {
        tvImageStr!!.text = imageStr
    }

    fun stringToImage(view: View) {
        imageView!!.setImageBitmap(resultBitmap)
    }
}
