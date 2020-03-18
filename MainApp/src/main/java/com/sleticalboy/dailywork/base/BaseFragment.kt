package com.sleticalboy.dailywork.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        var content = super.onCreateView(inflater, container, savedInstanceState)
        if (content == null) {
            content = inflater.inflate(layout(), container, false)
        }
        return content
    }

    @LayoutRes
    abstract fun layout(): Int
}