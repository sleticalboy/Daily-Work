package com.sleticalboy.dailywork.bean

import java.io.Serializable

/**
 * Created by AS on 18-1-29.
 *
 * @author leebin
 * @version 1.0
 * @description 股票实体类
 */
class Stock : Serializable {

    var errorNo: Int = 0
    var errorMsg: String? = null
    var data: List<DataEntity>? = null

    class DataEntity {
        var stockCode: String? = null
        var stockName: String? = null
        var exchange: String? = null
        var close: Double = 0.toDouble() // 今收-涨跌指数
        var high: Double = 0.toDouble() // 最高
        var low: Double = 0.toDouble() // 最低
        var netChange: Double = 0.toDouble() // 指数
        var netChangeRatio: Double = 0.toDouble() // 百分比
        var preClose: Double = 0.toDouble() // 昨收
        var open: Double = 0.toDouble() // 今开

        var capitalization: Long = 0
        var asset: Int = 0
        var volume: Int = 0
        var amplitudeRatio: Double = 0.toDouble()
        var stockStatus: String? = null
        var turnoverRatio: Double = 0.toDouble()
        var followNum: Int = 0
    }
}
