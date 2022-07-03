package com.lihb.wheelview

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import lihb.library.wheelview.NumericWheelAdapter
import lihb.library.wheelview.WheelView

class MainActivity : AppCompatActivity() {

    private val btn by lazy { findViewById<View>(R.id.btn) }
    private val btn_scroll_next by lazy { findViewById<View>(R.id.btn) }
    private val btn_select by lazy { findViewById<View>(R.id.btn) }
    private val btn_get_select by lazy { findViewById<View>(R.id.btn) }
    private val wheel_view2 by lazy { findViewById<WheelView>(R.id.wheel_view2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list = arrayListOf<String>()
        for (i in 0..50000) {
            list.add("SSSS$i")
        }

        val adapter = NumericWheelAdapter(this, 0, 50)
        adapter.setLabel("")
        adapter.textSize = 15
        adapter.textColor = Color.BLACK

//        val adapter = MyAdapter()
//        val adapter = object : BaseQuickWheelAdapter<String>(android.R.layout.simple_list_item_1) {
//            override fun onBindData(index: Int, bean: String, convertView: View) {
//                (convertView as TextView).text = bean
//            }
//        }

        adapter.setOnItemClickListener { wheel, adapter, index, isSelected ->
            Toast.makeText(this, "点击$index,$isSelected", Toast.LENGTH_SHORT).show()
        }
        adapter.setOnItemSelectedListener { wheel, adapter, index ->
            Toast.makeText(this, "最后选中的是$index", Toast.LENGTH_SHORT).show()
            Log.i(this.javaClass.simpleName, "onSelected:$index")
        }
//        adapter.setNewData(list)
        wheel_view2.adapter = adapter
//        adapter.setNewData(arrayListOf("0000000", "11111111", "22222222", "333333333"))

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
            Toast.makeText(this, "当前选中${adapter.currentIndex}", Toast.LENGTH_SHORT).show()
            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_selected_date_v2, null, false)
            val wheelYear = view.findViewById<WheelView>(R.id.wheel_year)
            val wheelMonth = view.findViewById<WheelView>(R.id.wheel_month)
            val wheelDay = view.findViewById<WheelView>(R.id.wheel_day)
            wheelYear.adapter = NumericWheelAdapter(this@MainActivity, 1990, 2118)
            wheelMonth.adapter = NumericWheelAdapter(this@MainActivity, 1, 12)
            wheelDay.adapter = NumericWheelAdapter(this@MainActivity, 1, 31)
            AlertDialog.Builder(this@MainActivity)
                    .setView(view)
                    .show()
        }
//        wheel_view2.setShadowColor(255,255,255)
//        wheel_view2.currentItem = 20
//        wheel_view2.addClickingListener { wheel, itemIndex ->
//            Toast.makeText(MainActivity@ this, "选中$itemIndex", Toast.LENGTH_SHORT).show()
//        }
        wheel_view2.addChangingListener { wheel, oldValue, newValue ->
            Log.i(this.javaClass.simpleName, "Changing $newValue")
        }
    }
}
