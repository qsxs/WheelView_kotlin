package com.lihb.wheelview

import android.view.View
import android.widget.TextView
import lihb.library.wheelview.BaseQuickWheelAdapter

class MyAdapter: BaseQuickWheelAdapter<String>(R.layout.item_wheel_view) {
    override fun onBindData(index: Int, bean: String, convertView: View) {
        (convertView as TextView).text = bean
    }
}