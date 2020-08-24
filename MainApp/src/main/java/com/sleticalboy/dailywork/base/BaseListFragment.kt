package com.sleticalboy.dailywork.base

import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleticalboy.dailywork.R
import kotlinx.android.synthetic.main.fragment_base_list.*

/**
 * Created on 20-8-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
abstract class BaseListFragment<DATA> : BaseFragment() {

    private var mAdapter: BaseRVAdapter<DATA>? = null

    override fun logTag(): String = "BaseListFragment"

    final override fun initView() {
        initHeader(headerContainer)

        listContainer.layoutManager = LinearLayoutManager(context)
        listContainer.adapter = getAdapter()

        initFooter(footerContainer)
    }

    fun getAdapter(): BaseRVAdapter<DATA> {
        if (mAdapter == null) {
            mAdapter = createAdapter()
        }
        return mAdapter!!
    }

    protected abstract fun createAdapter(): BaseRVAdapter<DATA>

    protected open fun initHeader(headerContainer: FrameLayout) {
    }

    protected open fun initFooter(footerContainer: FrameLayout) {
    }

    final override fun layout(): Int = R.layout.fragment_base_list
}