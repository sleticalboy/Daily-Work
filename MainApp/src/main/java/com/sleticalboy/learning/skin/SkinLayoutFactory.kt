package com.sleticalboy.learning.skin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import java.util.*

/**
 * Created on 20-9-10.
 *
 * @author binlee sleticalboy@gmail.com
 */
class SkinLayoutFactory : LayoutInflater.Factory2, Observer {

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return null
    }

    override fun update(o: Observable?, arg: Any?) {
        //
    }
}