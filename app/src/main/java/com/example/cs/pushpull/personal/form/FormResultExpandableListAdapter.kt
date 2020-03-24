package com.example.cs.pushpull.personal.form

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.RectangleTextView
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate

class FormResultExpandableListAdapter(context: Context, verifiedDateList: ArrayList<String>, stateList: ArrayList<Int>, nameList: ArrayList<String>, reasonList: ArrayList<String>, checkImage: Int) : BaseAdapter() {

    private var verified: ArrayList<String>? = null
    private var state: ArrayList<Int>? = null
    private var name: ArrayList<String>? = null
    private var reason: ArrayList<String>? = null
    private var check: Int? = null

    var inflater: LayoutInflater? = null

    init {
        name = nameList
        verified = verifiedDateList
        state = stateList
        check = checkImage
        reason = reasonList
        inflater = LayoutInflater.from(context)
    }

    override fun getItem(position: Int) = verified!![position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = verified!!.size

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
        inflater!!.inflate(R.layout.item_form_result, parent, false).apply {
            val formCircularTextView: RectangleTextView = findViewById(R.id.form_item_circular_text_view)
            val title: TextView = findViewById(R.id.form_item_title)
            val dateText: TextView = findViewById(R.id.form_item_date)
            val img: ImageView = findViewById(R.id.form_item_result_state)
            val reasonText: TextView = findViewById(R.id.from_item_reason)

            formCircularTextView.run {
                text = if (state!![position] == 0) "證照" else "成績進步"
                setSolidColor(resources.getStringArray(R.array.course_type_color)[state!![position]])
            }

            title.text = name!![position]
            dateText.text = verified!![position].toDate().formatTo("yyyy/MM/dd").replace("/",".")

            if(check == 0){
                img.setImageResource(R.drawable.ic_apply_succeed)
                reasonText.text = "通過"
            }
            else if(check == 1){
                img.setImageResource(R.drawable.ic_apply_fail)
                reasonText.text = reason!![position]
            }
        }!!
}
