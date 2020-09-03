package com.sleticalboy.learning.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment

abstract class BaseDialog : DialogFragment() {

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        configureDialog(dialog)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                    savedInstanceState: Bundle?): View? {
        var content = super.onCreateView(inflater, container, savedInstanceState)
        if (content == null) {
            content = inflater.inflate(layout(), container, false)
        }
        return content
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
    }

    abstract fun initView(view: View)

    @LayoutRes
    abstract fun layout(): Int

    protected  open fun configureDialog(dialog: Dialog) {
    }

    protected open fun logTag(): String = "BaseDialog"
}