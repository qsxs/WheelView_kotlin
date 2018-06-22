package com.lihb.library

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.StyleRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout

class WheelViewV2 : FrameLayout {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        init(context, attr)
    }

    constructor(context: Context, attr: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        init(context, attr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attr: AttributeSet?, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attr, defStyleAttr, defStyleRes) {
        init(context, attr)
    }

    private fun init(context: Context, attr: AttributeSet?) {
        val list = RecyclerView(context)
        list.layoutManager = LinearLayoutManager(context)
        list.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val adapter = WheelViewAdapter()
        adapter.setNewData(listOf("HHH1","HHH2","HHH3","HHH4","HHH5","HHH6","HHH7","HHH8","HHH9","HHH1","HHH2","HHH3","HHH4","HHH5","HHH6","HHH7","HHH8","HHH9"))
        list.adapter = adapter
        addView(list)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            startScrollerTask()
        }
        return super.onTouchEvent(ev)
    }

    private fun startScrollerTask() {

//        initialY = scrollY
//        this.postDelayed(scrollerTask, newCheck.toLong())
    }
}