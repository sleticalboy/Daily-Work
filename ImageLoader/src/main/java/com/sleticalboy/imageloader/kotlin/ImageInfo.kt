package com.sleticalboy.imageloader.kotlin

/**
 * Created on 18-4-28.
 * @author sleticalboy
 * @description
 */
class ImageInfo(private var uri: String, private var cacheKey: String) {
    init {
        this.uri = uri
        this.cacheKey = cacheKey
    }
}