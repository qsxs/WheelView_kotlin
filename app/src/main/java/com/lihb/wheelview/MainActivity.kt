package com.lihb.wheelview

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.lihb.library.WheelView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = arrayListOf<String>()
        for (i in 0..50){
            data.add("HAHAHHAHAHHAHHHAHAH $i")
        }
        wheel_view.setItems(data)
        wheel_view.selectedIndex = 0
        wheel_view.setCycleDisable(false)
        wheel_view.setVisibleItemCount(5)
        wheel_view.setTextSize(15F)
//        wheel_view.setDividerColor(Color.BLUE)
        val dividerConfig = WheelView.DividerConfig()
        dividerConfig.setColor(Color.BLUE)
        dividerConfig.setRatio(0F)
        wheel_view.setDividerConfig(dividerConfig)
        wheel_view.setLabel("label",false)
        wheel_view.setTextColor(Color.GREEN)
        wheel_view.setTextSizeAutoFit(false)
//        wheel_view.setUseWeight(false)
//        wheel_view.setGravity(Gravity.RIGHT)
        wheel_view.setOnItemSelectListener { index ->
            Toast.makeText(MainActivity@this,"选中$index",Toast.LENGTH_SHORT).show()
        }

    }
}
