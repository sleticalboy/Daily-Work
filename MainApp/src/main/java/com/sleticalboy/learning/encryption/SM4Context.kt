package com.sleticalboy.learning.encryption


class SM4Context {
    /**
     * 加密模式
     */
    private var mode = 1
    private var sk: LongArray
    private var isPadding = true
    fun getMode(): Int {
        return mode
    }

    fun setMode(mode: Int) {
        this.mode = mode
    }

    fun getSk(): LongArray {
        return sk
    }

    fun setSk(sk: LongArray) {
        this.sk = sk
    }

    fun isPadding(): Boolean {
        return isPadding
    }

    fun setPadding(padding: Boolean) {
        isPadding = padding
    }

    init {
        sk = LongArray(32)
    }
}