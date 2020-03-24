package com.example.cs.pushpull.school.rollcall.scan

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cs.pushpull.R

class ScanRecordListAdapter(context: Context, uuidList: ArrayList<String>, nameList: ArrayList<String>, timeList: ArrayList<String>): BaseAdapter() {

    private var uuid: ArrayList<String>? = null
    private var name: ArrayList<String>? = null
    private var time: ArrayList<String>? = null
    private var inflater: LayoutInflater? = null

    init {
        uuid = uuidList
        name = nameList
        time = timeList
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater!!.inflate(R.layout.item_rollcall_record_list, parent, false).apply {

        }
    }

    override fun getItem(position: Int): Any {
        return uuid!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return uuid!!.size
    }
}