package com.sleticalboy.imageloader.kotlin

import kotlin.concurrent.thread

/**
 * Created on 18-4-28.
 * @author sleticalboy
 * @description
 */

class Test {

    fun test() {
        thread { MyTask("my thread") }.start()
    }
}