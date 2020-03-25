package com.sleticalboy.dailywork.bean

data class ModuleItem @JvmOverloads constructor(
        var title: String = "",
        var cls: String = "",
        var clazz: Class<*>?
)