package com.binlee.emoji.helper

import android.text.TextUtils
import java.io.File

class UrlHelper private constructor() {

    companion object {
        fun inspectUrl(url: String?): String? {
            var url = url
            if (TextUtils.isEmpty(url)) {
                return url
            }
            if (isFile(url) || isHttp(url) || isAndroid(url)) {
                return url
            }
            if (!isHttp(url) && url!!.startsWith("/")) {
                url = baseUrl() + url
            } else if (!isHttp(url) && !url!!.startsWith("/")) {
                url = baseUrl() + File.separator + url
            }
            return url
        }

        fun isFile(url: String?): Boolean {
            return url != null && url.startsWith("file://")
        }

        fun isHttp(url: String?): Boolean {
            return url != null && url.startsWith("http")
        }

        fun isAndroid(url: String?): Boolean {
            return url != null && url.startsWith("android.")
        }

        fun baseUrl(): String {
            return ""
        }
    }

    init {
        throw AssertionError("no instance.")
    }
}