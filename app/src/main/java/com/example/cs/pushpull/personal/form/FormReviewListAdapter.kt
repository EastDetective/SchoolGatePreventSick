package com.example.cs.pushpull.personal.form

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.CircularTextView
import com.example.cs.pushpull.extension.RectangleTextView
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate

class FormReviewListAdapter(context: Context, nameList: ArrayList<String>, dateList: ArrayList<String>, stateList: ArrayList<Int>) : BaseAdapter() {

    private var name: ArrayList<String>? = null
    private var date: ArrayList<String>? = null
    private var state: ArrayList<Int>? = null

    var inflater: LayoutInflater? = null

    init {
        name = nameList
        date = dateList
        state = stateList
        inflater = LayoutInflater.from(context)
    }

    override fun getItem(position: Int) = name!![position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = name!!.size

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
        inflater!!.inflate(R.layout.item_form_review, parent, false).apply {
            val formCircularTextView: RectangleTextView = findViewById(R.id.form_item_circular_text_view)
            val title: TextView = findViewById(R.id.form_item_title)
            val dateText: TextView = findViewById(R.id.form_item_date)

            formCircularTextView.run {
                text = if (state!![position] == 0) "證照" else "成績進步"
                setSolidColor(resources.getStringArray(R.array.course_type_color)[state!![position]])
            }

            title.text = name!![position]
            dateText.text = date!![position].toDate().formatTo("yyyy/MM/dd").replace("/",".")
        }!!
}