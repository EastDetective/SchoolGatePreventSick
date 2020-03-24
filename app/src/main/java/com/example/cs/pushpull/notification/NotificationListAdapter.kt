package com.example.cs.pushpull.notification

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.RectangleTextView
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate
import com.example.cs.pushpull.notification.model.NotificationModel

class NotificationListAdapter(
    context: Context,
    private val notification: List<NotificationModel.Notice>
) : BaseAdapter() {

    private var title: List<String>? = null
    private var time: List<String>? = null
    private var content: List<String>? = null
    private var courseType: List<Int>? = null
    private var inflater: LayoutInflater? = null

    init {
        title = notification.map { it.title }
        time = notification.map { it.pushTime }
        content = notification.map { it.content }
        courseType = notification.map { it.courseType }
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        inflater!!.inflate(R.layout.item_notification, parent, false).apply {
            val titleTextView: TextView = findViewById(R.id.notificationRow_title)
            titleTextView.text = title!![position]

            val t: Int
            val timeTextView: TextView = findViewById(R.id.notificationRow_time)
            if (time!![position].toDate().formatTo("HH").toInt() > 12) {
                t = time!![position].toDate().formatTo("HH").toInt() - 12
                timeTextView.text = time!![position].toDate().formatTo("MM月dd日下午") + t.toString() + "點"
            } else {
                timeTextView.text = time!![position].toDate().formatTo("MM月dd日上午HH點")
            }

            val contentTitleTextView: TextView = findViewById(R.id.notificationRow_content_title)
            contentTitleTextView.text = title!![position] + "內容："

            val contentTextView: TextView = findViewById(R.id.notificationRow_content)
            contentTextView.text = content!![position]

            val timeTextViewExpand: TextView = findViewById(R.id.notificationRow_time2)
            timeTextViewExpand.text = timeTextView.text

            val circularTextView: RectangleTextView = findViewById(R.id.notificationRow_item_circular_text_view)
            circularTextView.text = resources.getStringArray(R.array.course_type_text)[courseType!![position]]
            circularTextView.setSolidColor(resources.getStringArray(R.array.course_type_color)[courseType!![position]])

            val expandArrow: ImageView = findViewById(R.id.notificationRow_item_arrow)
            val upPart: RelativeLayout = findViewById(R.id.notificationRow_item_up_part_layout)

            if (notification[position].isExtended) {
                upPart.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                expandArrow.rotation = 180f
            } else {
                upPart.layoutParams.height = resources.getDimension(R.dimen.notificationRow_collapse_height).toInt()
                expandArrow.rotation = 0f
            }

            // About Expanding and Collapsing
            expandArrow.setOnClickListener {
                if (notification[position].isExtended) {
                    it.animate().setDuration(300).rotation(0f).start()
                    upPart.layoutParams.height = resources.getDimension(R.dimen.notificationRow_collapse_height).toInt()
                } else {
                    it.animate().setDuration(300).rotation(180f).start()
                    upPart.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                }
                notification[position].isExtended = !notification[position].isExtended
                upPart.requestLayout()
            }
        }

    override fun getItem(position: Int) = title!![position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = title!!.size

}