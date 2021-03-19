package com.sleticalboy.http

/**
 * Created on 18-3-26.
 *
 * @author leebin
 * @description
 */
interface Constants {

    companion object {
        // const val LIVE_HOST = "http://www.baidu.com"
        const val LIVE_HOST = "https://api.github.com"
        const val API_QUERY = "/api/v2/yrrcb/bap/query" // 查询
        const val API_REGISTER = "/api/v2/yrrcb/bap/regis" // 注册
        const val API_JUDGE = "/api/bap/judge" // 验证
    }
}