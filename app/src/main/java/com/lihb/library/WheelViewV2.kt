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
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//                Log.i("TAG","onScrollStateChanged:$newState")
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView?.scrollY
                Log.i("TAG","onScrolled:${recyclerView?.scrollY},$dy")
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            list.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//                Log.i("TAG","setOnScrollChangeListener:$scrollY,$oldScrollY")
            }
        }
        list.layoutManager = LinearLayoutManager(context)
        list.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val adapter = WheelViewAdapter()
        var data:ArrayList<String> = arrayListOf()
        for (i in 1..100){
            data.add("HHH$i")
        }
        adapter.setNewData(data)
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

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        Log.i("TAG","onScrollChanged：$l,$t,$oldl,$oldt")
    }

    override fun onStopNestedScroll(child: View?) {
        super.onStopNestedScroll(child)
        Log.i("TAG", "onStopNestedScroll")
    }

    override fun onNestedScrollAccepted(child: View?, target: View?, axes: Int) {
        super.onNestedScrollAccepted(child, target, axes)
        Log.i("TAG", "onNestedScrollAccepted $axes")
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        Log.i("TAG", "onOverScrolled $scrollX,$scrollY,$clampedX,$clampedY")
    }

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
        super.onNestedPreScroll(target, dx, dy, consumed)
        Log.i("TAG", "onNestedPreScroll $dx,$dy,$consumed")
    }

    private fun startScrollerTask() {

//        initialY = scrollY
//        this.postDelayed(scrollerTask, newCheck.toLong())
    }
}