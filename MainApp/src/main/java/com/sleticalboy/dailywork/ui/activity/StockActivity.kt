package com.sleticalboy.dailywork.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.dailywork.bean.Stock
import com.sleticalboy.dailywork.http.HttpUtils

import java.io.IOException
import java.text.DecimalFormat

import butterknife.ButterKnife
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody

class StockActivity : BaseActivity() {

    override fun initData() {
        val url = "https://gupiao.baidu.com/api/rails/stockbasicbatch?format=json&stock_code=hk02799"
        HttpUtils.request(url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(mainLooper).post { Toast.makeText(this@StockActivity, "网络异常", Toast.LENGTH_SHORT).show() }
                Log.d("error", e.message)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val vBody = response.body() ?: return
                val vJson = vBody.string() ?: return
                val vStock = Gson().fromJson(vJson, Stock::class.java)
                if (vStock.data != null && vStock.data!!.isNotEmpty()) {
                    Handler(mainLooper).post { setStockView(vStock.data!![0]) }
                }
            }
        })
    }

    override fun initView() {

    }

    override fun attachLayout(): Int {
        return R.layout.activity_main
    }

    private fun setStockView(stock: Stock.DataEntity) {
        val decimalFormat = DecimalFormat("0.000")

        val tv_stock_close = findViewById<View>(R.id.tv_stock_close) as TextView
        tv_stock_close.text = String.format("%s↑", decimalFormat.format(stock.close))

        val stockNetchange = findViewById<View>(R.id.tv_stock_netchange) as TextView
        stockNetchange.text = String.format("+%s", decimalFormat.format(stock.netChange))

        val stockNetchangeRatio = findViewById<View>(R.id.tv_stock_netchange_ratio) as TextView
        stockNetchangeRatio.text = String.format("+%s%%", decimalFormat.format(stock.netChangeRatio))

        // 昨收
        val stockPreclose = findViewById<View>(R.id.tv_stock_preclose) as TextView
        stockPreclose.text = String.format("昨收 %s", decimalFormat.format(stock.preClose))

        val stockOpen = findViewById<View>(R.id.tv_stock_open) as TextView
        val openString = SpannableString(String.format("今开 %s", decimalFormat.format(stock.open)))
        stockOpen.text = setSpans(openString, "#FF4B4F")

        val stockHigh = findViewById<View>(R.id.tv_stock_high) as TextView
        val highString = SpannableString(String.format("最高 %s", decimalFormat.format(stock.high)))
        stockHigh.text = setSpans(highString, "#FF4B4F")

        val stockLow = findViewById<View>(R.id.tv_stock_low) as TextView
        val lowString = SpannableString(String.format("最低 %s", decimalFormat.format(stock.low)))
        stockLow.text = setSpans(lowString, "#17C296")
    }

    private fun setSpans(target: SpannableString, color: String): SpannableString {
        val startSpan = ForegroundColorSpan(Color.BLACK)
        val endSpan = ForegroundColorSpan(Color.parseColor(color))
        target.setSpan(startSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        target.setSpan(endSpan, 2, target.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return target
    }

}
