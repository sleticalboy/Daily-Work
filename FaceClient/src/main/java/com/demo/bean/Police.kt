package com.demo.bean

import java.io.Serializable

/**
 * Created on 18-2-27.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
class Police : Serializable {

    var cn: String? = null // 姓名
    var t: String? = null // TF 卡标识号
    var g: String? = null // 警号
    var alias: String? = null // 身份证号码
    var s: String? = null // 省份
    var l: String? = null // 市
    var o: String? = null // 组织
    var ou: String? = null // 机构
    var e: String? = null // 电子邮件
    var i: String? = null // 容器名称

    override fun toString(): String {
        return "Police{" +
                "CN='" + cn + '\'' +
                ", T='" + t + '\'' +
                ", G='" + g + '\'' +
                ", ALIAS='" + alias + '\'' +
                ", S='" + s + '\'' +
                ", L='" + l + '\'' +
                ", O='" + o + '\'' +
                ", OU='" + ou + '\'' +
                ", E='" + e + '\'' +
                ", I='" + i + '\'' +
                '}'
    }
}
