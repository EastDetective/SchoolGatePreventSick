package com.example.cs.pushpull.personal

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cs.pushpull.R

class PointListAdapter(context: Context, reasonList: ArrayList<String>, getList: ArrayList<Int>): BaseAdapter() {

    private var reason: ArrayList<String>? = null
    private var get: ArrayList<Int>? = null
    private var inflater: LayoutInflater? = null

    init {
        reason = reasonList
        get = getList
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater!!.inflate(R.layout.item_point, parent, false).apply {
            val earnReason: TextView = findViewById(R.id.pointRow_earn_reason)
            earnReason.text = reason!![position]
            val earnPoint: TextView = findViewById(R.id.pointRow_earn_point)
            if(get!![position] > 0){
                earnPoint.text = "+"+get!![position].toString()
            }
            else
                earnPoint.text = get!![position].toString()
        }
    }

    override fun getItem(position: Int): Any {
        return reason!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return reason!!.size
    }

}