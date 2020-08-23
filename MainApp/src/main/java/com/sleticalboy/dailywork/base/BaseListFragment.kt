package com.sleticalboy.dailywork.base

import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sleticalboy.dailywork.R
import kotlinx.android.synthetic.main.fragment_base_list.*

/**
 * Created on 20-8-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
abstract class BaseListFragment<DATA> : BaseFragment() {

    override fun logTag(): String = "BaseListFragment"

    final override fun initView() {
        initHeader(headerContainer)

        listView().layoutManager = LinearLayoutManager(context)
        listView().adapter = createAdapter()

        initFooter(footerContainer)
    }

    abstract fun createAdapter(): BaseRVAdapter<DATA>

    protected open fun initHeader(headerContainer: FrameLayout) {
    }

    protected open fun initFooter(footerContainer: FrameLayout) {
    }

    final override fun layout(): Int = R.layout.fragment_base_list

    protected fun listView(): RecyclerView = listContainer
}