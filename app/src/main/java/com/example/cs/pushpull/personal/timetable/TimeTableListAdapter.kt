package com.example.cs.pushpull.personal.timetable

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.CircularTextView

class TimeTableListAdapter(
    context: Context,
    typeList: ArrayList<Int>,
    timeList: ArrayList<String>,
    nameList: ArrayList<String>
) : BaseAdapter() {

    private var inflater: LayoutInflater? = null

    // Information of Classes
    private var type: ArrayList<Int>? = null
    private var time: ArrayList<String>? = null
    private var name: ArrayList<String>? = null

    init {
        type = typeList
        time = timeList
        name = nameList
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("NewApi", "ViewHolder", "ResourceType")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return inflater!!.inflate(R.layout.item_timetable, parent, false).apply {
            val showType: CircularTextView = findViewById(R.id.timetableRow_circular_tv)
            when (type!![position]) {
                0, 1, 2 -> showType.run {
                    setSolidColor(resources.getStringArray(R.array.course_type_color)[type!![position]])
                }
                else -> showType.run {
                    setSolidColor(resources.getStringArray(R.array.course_type_color)[3])
                }
            }

            val classTime: TextView = findViewById(R.id.timetableRow_time)
            if (time != null && time!!.size > 0) {
                classTime.text = time!![position]
            }

            val className: TextView = findViewById(R.id.timetableRow_class_name)
            if (name != null && name!!.size > 0) {
                className.text = name!![position]
            }
        }
    }

    override fun getItem(position: Int): Any {
        return type!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return type!!.size
    }

}