package com.sleticalboy.imageloader.kotlin

/**
 * Created on 18-4-28.
 * @author sleticalboy
 * @description
 */

class MyTask(private val name: String) : Runnable {

    init {
        println("constructor")
    }

    override fun run() {
        println(name + "run")
    }

}