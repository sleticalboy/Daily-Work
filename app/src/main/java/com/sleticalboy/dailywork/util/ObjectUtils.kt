package com.sleticalboy.dailywork.util

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
object ObjectUtils {

    fun equals(actual: Any?, excepted: Any?): Boolean {
        return actual === excepted || if (actual == null) excepted == null else actual == excepted
    }
}
