package lihb.library.wheelview

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

abstract class BaseQuickWheelAdapter<T> : AbstractWheelAdapter {
    private var mData: List<T>? = null
    private var itemResourceId: Int = 0

    val data: List<T>
        get() = if (mData == null) ArrayList() else mData!!

    override val itemsCount: Int
        get() = if (mData == null) 0 else mData!!.size

    constructor(@LayoutRes itemLayoutId: Int) {
        itemResourceId = itemLayoutId
    }

    constructor(@LayoutRes itemLayoutId: Int, data: List<T>) {
        itemResourceId = itemLayoutId
        mData = data
    }

    fun setNewData(data: List<T>) {
        mData = data
    }

    override fun getItem(index: Int, itemView: View?, parent: ViewGroup?): View? {
        var convertView = itemView
        if (index in 0..(itemsCount - 1)) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent!!.context).inflate(itemResourceId, parent, false)
            }
            onBindData(index, mData!![index], convertView!!)
            return convertView
        }
        return null
    }

    fun getItemBean(index: Int): T? {
        return if (mData != null && index >= 0 && index == itemsCount) {
            mData!![index]
        } else {
            null
        }
    }

    protected abstract fun onBindData(index: Int, bean: T, convertView: View)
}
