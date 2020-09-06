package com.binlee.emoji

import com.binlee.emoji.compat.HttpEngine
import com.binlee.emoji.third.OkHttpEngine

class HttpAdapter private constructor() {

    companion object {

        private lateinit var sEngine: HttpEngine<*, *>

        fun setHttpEngine(engine: HttpEngine<*, *>?) {
            if (engine == null) {
                throw NullPointerException("engine is null")
            }
            sEngine = engine
        }

        fun engine(): HttpEngine<*, *> {
            if (sEngine == null) {
                sEngine = OkHttpEngine()
                // sEngine = new DefaultHttpEngine();
                // sEngine = new SocketHttpEngine();
            }
            return sEngine
        }
    }

    init {
        throw AssertionError("no instance.")
    }
}