package com.sleticalboy.learning.data


/**
 * Created on 20-3-31.
 *
 * @author binlee sleticalboy@gmail.com
 */
open class Result<T> {
    class Success<Data>(private val mData: Data) : Result<Data>() {
        fun getData(): Data {
            return mData
        }
    }

    class Error(private val mCause: Throwable) : Result<Any?>() {
        fun getCause(): Throwable {
            return mCause
        }
    }

    class Loading : Result<Any?>()

    override fun toString(): String {
        if (this is Success<*>) {
            return "data = " + (this as Success<T>).getData()
        }
        if (this is Error) {
            return "error = " + (this as Error).getCause()
        }
        return if (this is Loading) {
            "Loading..."
        } else super.toString()
    }
}