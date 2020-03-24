package com.example.cs.pushpull.game

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate

class HistoryListAdapter(context: Context, dateList: ArrayList<String>) : BaseAdapter() {

    private var date: ArrayList<String>? = null
    private var inflater: LayoutInflater? = null

    init {
        date = dateList
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater!!.inflate(R.layout.item_history, parent, false).apply {
            val usedDate: TextView = findViewById(R.id.used_date)
            usedDate.text = date!![position].toDate().formatTo("yyyy/MM/dd").replace("/",".")
        }
    }

    override fun getItem(position: Int): Any {
        return date!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return date!!.size
    }

}