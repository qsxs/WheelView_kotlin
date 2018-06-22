package com.lihb.library

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.lihb.wheelview.R

class WheelViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    var mData: List<String>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflate = LayoutInflater.from(parent.context).inflate(R.layout.item_wheel_view, parent,false)
        return object : RecyclerView.ViewHolder(inflate) {

        }
    }

    override fun getItemCount(): Int {
        return if (mData != null) {
            mData!!.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = mData!![position]
    }

    constructor()

    fun setNewData(data: List<String>?) {
        mData = data
        notifyDataSetChanged()
    }
}