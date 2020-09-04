package com.sleticalboy.weight.view

import android.content.Context
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener


/**
 * Created on 18-3-15.
 *
 * @author leebin
 * @description 圆点页面指示器
 */
class CommonPageIndicator @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle), PageIndicator {
    private val mRadius = 0
    private var mCurrentPage = 0
    private var mViewPager: ViewPager? = null
    private val mInternalListener: OnPageChangeListener? = null
    private fun init() {}
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.mCurrentPage = mCurrentPage
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mCurrentPage = savedState.mCurrentPage
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(widthMeasureSpec)
        val specSize = MeasureSpec.getSize(widthMeasureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = 2 * mRadius + paddingTop + paddingBottom + 1
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    private fun measureHeight(heightMeasureSpec: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        val specSize = MeasureSpec.getSize(heightMeasureSpec)
        if (specMode == MeasureSpec.EXACTLY || mViewPager == null || mViewPager!!.adapter == null) {
            result = specSize
        } else {
            val count = mViewPager!!.adapter!!.count
            result = paddingLeft + paddingRight + 2 * count * mRadius + (count - 1) * mRadius + 1
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    internal class SavedState : BaseSavedState {
        var mCurrentPage = 0

        constructor(superState: Parcelable?) : super(superState)
        private constructor(`in`: Parcel) : super(`in`) {
            mCurrentPage = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(mCurrentPage)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    override fun setupWithViewPager(pagerView: ViewPager?, initialPos: Int) {
        mViewPager = checkValid<ViewPager?>(mViewPager)
        checkNotNull(mViewPager!!.adapter) { "没有设置 Adapter" }
        mViewPager = pagerView
        invalidate()
    }

    private fun <T> checkValid(ref: T?): T {
        requireNotNull(ref)
        return ref
    }

    override fun setCurrentPage(pageIndex: Int) {
        mCurrentPage = pageIndex
        invalidate()
    }

    override fun notifyDataSetChanged() {
        invalidate()
    }

    companion object {
        private const val INVALID_POINTER = -1
    }

    init {
        init()
    }
}