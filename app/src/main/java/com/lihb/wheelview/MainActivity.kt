package com.lihb.wheelview

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = arrayListOf<String>()
        for (i in 0..50000) {
            list.add("SSSS$i")
        }

//        val adapter = NumericWheelAdapter(this, 0, 50)
//        adapter.setLabel("")
//        adapter.textSize = 15
//        adapter.textColor = Color.BLACK
        val adapter = MyAdapter()
//        val adapter = object : BaseQuickWheelAdapter<String>(android.R.layout.simple_list_item_1) {
//            override fun onBindData(index: Int, bean: String, convertView: View) {
//                (convertView as TextView).text = bean
//            }
//        }

        adapter.setOnItemClickListener { wheel, adapter, index, isSelected ->
            Toast.makeText(MainActivity@ this, "点击$index,$isSelected", Toast.LENGTH_SHORT).show()
        }
        adapter.setOnItemSelectedListener { wheel, adapter, index ->
            Toast.makeText(MainActivity@ this, "最后选中的是$index", Toast.LENGTH_SHORT).show()
            Log.i(MainActivity@ this.javaClass.simpleName, "onSelected:$index")
        }
        adapter.setNewData(list)
        wheel_view2.adapter = adapter
        adapter.setNewData(arrayListOf("0000000", "11111111", "22222222", "333333333"))

        wheel_view2.setCyclic(false)
        wheel_view2.isGradual = false
        wheel_view2.visibleItems = 5
        wheel_view2.divingWidth = 10
        wheel_view2.divingColor = Color.YELLOW

        btn.setOnClickListener {
            val toInt = (Math.random() * list.size).toInt()
//            Toast.makeText(MainActivity@ this, "滚动到$toInt", Toast.LENGTH_SHORT).show()
            adapter.scrollTo(toInt, true)
        }
        btn_scroll_next.setOnClickListener {
            val toInt = adapter.currentIndex + 1
//            Toast.makeText(MainActivity@ this, "滚动到$toInt", Toast.LENGTH_SHORT).show()
            adapter.scrollTo(toInt, true)
        }
        btn_select.setOnClickListener {
            val toInt = (Math.random() * list.size).toInt()
//            Toast.makeText(MainActivity@ this, "选中到$toInt", Toast.LENGTH_SHORT).show()
            adapter.scrollTo(toInt)
        }
        btn_get_select.setOnClickListener {
            Toast.makeText(MainActivity@ this, "当前选中${adapter.currentIndex}", Toast.LENGTH_SHORT).show()
        }
//        wheel_view2.setShadowColor(255,255,255)
//        wheel_view2.currentItem = 20
//        wheel_view2.addClickingListener { wheel, itemIndex ->
//            Toast.makeText(MainActivity@ this, "选中$itemIndex", Toast.LENGTH_SHORT).show()
//        }
        wheel_view2.addChangingListener { wheel, oldValue, newValue ->
            Log.i(MainActivity@ this.javaClass.simpleName, "Changing $newValue")
        }
    }
}
