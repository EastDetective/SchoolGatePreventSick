package com.example.cs.pushpull.school.previous

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.*
import com.example.cs.pushpull.school.allcourse.AllCourseListAdapter
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.previous.response.ResponseFragment
import com.example.cs.pushpull.school.model.CourseModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.item_my_course.view.*

class MyAttendedCourseListAdapter(
    context: Context,
    courseList: List<CourseModel.Course>,
    courseApiService: CourseApiService,
    disposable: Disposable?,
    studentId: String,
    tabName: String,
    activity: FragmentActivity
) : AllCourseListAdapter(context, courseList, courseApiService, disposable, studentId) {

    private var tabName: String? = null
    private var activity: FragmentActivity? = null

    init {
        this.tabName = tabName
        course = courseList
        this.activity = activity
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("SetTextI18n", "ResourceType", "PrivateResource")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return if (tabName == inflater!!.inflate(
                R.layout.item_my_course,
                parent,
                false
            ).resources.getStringArray(R.array.my_course_tab)[0]
        ) {
            inflater!!.inflate(R.layout.item_my_course, parent, false).apply {

                // Nodes Binding
                val circularTextView: RectangleTextView = findViewById(R.id.my_course_item_circular_text_view)
                val nameTextView: TextView = findViewById(R.id.my_course_item_name)
                val numOfApplyTextView: TextView = findViewById(R.id.my_course_item_num_of_student)
                val stateTextView: TextView = findViewById(R.id.my_course_item_state)
                val introTextView: TextView = findViewById(R.id.my_course_item_intro)
                val teacherTextView: TextView = findViewById(R.id.my_course_item_course_teacher_content)
                val targetStdTextView: TextView = findViewById(R.id.my_course_item_target_student_content)
                val numOfStdTextView: TextView = findViewById(R.id.my_course_item_num_of_student_content)
                val campusTextView: TextView = findViewById(R.id.my_course_item_campus_content)
                val dateTextView: TextView = findViewById(R.id.my_course_item_course_date_content)
                val placeTextView: TextView = findViewById(R.id.my_course_item_course_place_content)
                val applyTimeTextView: TextView = findViewById(R.id.my_course_item_apply_date_content)
                val costTextView: TextView = findViewById(R.id.my_course_item_course_cost_content)
                val applyMethodTextView: TextView = findViewById(R.id.my_course_item_apply_method_content)
                val enrollPostTextView: TextView = findViewById(R.id.my_course_item_enroll_post_content)
                val otherTextView: TextView = findViewById(R.id.my_course_item_other_content)
                val checkFavorite: CheckBox = findViewById(R.id.my_course_item_check_favorite)
                val rightButton: Button = findViewById(R.id.my_course_item_right_button)
                val leftButton: Button = findViewById(R.id.my_course_item_left_button)
                val listButton: Button = findViewById(R.id.my_course_item_list_button)
                val cellCheck: CheckBox = findViewById(R.id.my_course_item_checkbox_btn)
                val expandArrow: ImageView = findViewById(R.id.my_course_item_arrow)
                val upPart: RelativeLayout = findViewById(R.id.my_course_item_up_part_layout)
                val voteWeek: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0)

                // Fill Basic Info into every Nodes
                when (course!![position].courseType) {
                    0, 1, 2 -> circularTextView.run {
                        text = resources.getStringArray(R.array.course_type_text)[course!![position].courseType]
                        setSolidColor(resources.getStringArray(R.array.course_type_color)[course!![position].courseType])
                    }
                    else -> circularTextView.run {
                        text = resources.getString(R.string.course_type_text_error)
                        setSolidColor(resources.getStringArray(R.array.course_type_color)[3])
                    }
                }
                nameTextView.apply {
                    text = course!![position].courseName
                    setSingleLine()
                }
                numOfApplyTextView.text = when (course!![position].status) {
                    0, 1, 2 -> "${course!![position].numberOfVoters}" + "人已投票"
                    3, 4, 5, 6, 7, 8, 9, 10 -> "${course!![position].numberOfApply}" + "人報名中"
//                    9, 10 -> "${course!![position].numberOfApply}" + "人上課"
                    else -> ""
                }
                stateTextView.text = when (course!![position].status) {
                    0 -> "尚未開始投票"
                    1 -> "投票中"
                    2 -> "投票截止"
                    3 -> "尚未開始報名"
                    4 -> "一階段報名中"
                    5 -> "一階段報名截止"
                    6 -> "二階段報名中"
                    7 -> "報名截止"
                    8 -> "尚未開始上課"
                    9 -> "上課中"
                    10 -> "上課結束"
                    11 -> "未審核"
                    12 -> "審核通過"
                    13 -> "審核未通過"
                    14 -> "投票通過"
                    15 -> "投票未通過"
                    else -> "Error"
                }
                introTextView.text = course!![position].courseIntroduction
                if (course!![position].status in 0..2) {
                    campusTextView.text = if (course!![position].campus == 0) "博愛校區" else "天母校區"

                    // Set Unused Node to INVISIBLE
                    val numOfStdTitle: TextView = findViewById(R.id.my_course_item_num_of_student_title)
                    numOfStdTitle.visibility = View.GONE
                    numOfStdTextView.visibility = View.GONE
                    val teacherTitle: TextView = findViewById(R.id.my_course_item_course_teacher_title)
                    teacherTitle.visibility = View.GONE
                    teacherTextView.visibility = View.GONE
                    val targetStdTitle: TextView = findViewById(R.id.my_course_item_target_student_title)
                    targetStdTitle.visibility = View.GONE
                    targetStdTextView.visibility = View.GONE
                    val dateTitle: TextView = findViewById(R.id.my_course_item_course_date_title)
                    dateTitle.visibility = View.GONE
                    dateTextView.visibility = View.GONE
                    val placeTitle: TextView = findViewById(R.id.my_course_item_course_place_title)
                    placeTitle.visibility = View.GONE
                    placeTextView.visibility = View.GONE
                    val applyTimeTitle: TextView = findViewById(R.id.my_course_item_apply_date_title)
                    applyTimeTitle.visibility = View.GONE
                    applyTimeTextView.visibility = View.GONE
                    val costTitle: TextView = findViewById(R.id.my_course_item_course_cost_title)
                    costTitle.visibility = View.GONE
                    costTextView.visibility = View.GONE
                    val applyMethodTitle: TextView = findViewById(R.id.my_course_item_apply_method_title)
                    applyMethodTitle.visibility = View.GONE
                    applyMethodTextView.visibility = View.GONE
                    val enrollPostTitle: TextView = findViewById(R.id.my_course_item_enroll_post_title)
                    enrollPostTitle.visibility = View.GONE
                    enrollPostTextView.visibility = View.GONE
                    val otherTitle: TextView = findViewById(R.id.my_course_item_other_title)
                    otherTitle.visibility = View.GONE
                    otherTextView.visibility = View.GONE
                    my_course_item_apply_result_title.visibility = View.GONE
                    my_course_item_apply_result_content.visibility = View.GONE
                } else {
                    teacherTextView.text =
                        if (course!![position].courseTeacher == "") "未定" else course!![position].courseTeacher
                    targetStdTextView.text = when (course!![position].targetStudent) {
                        0 -> "一般生"
                        else -> "未定義"
                    }
                    numOfStdTextView.run {
                        text =
                            "第一階段：" + course!![position].firstNumberOfStudents.toString() + "\n" + "第二階段：" + course!![position].secondNumberOfStudents.toString()
                    }
                    campusTextView.text = if (course!![position].campus == 0) "博愛校區" else "天母校區"
                    dateTextView.run {
                        for (i in 0 until course!![position].courseDate.size) {
                            text = if (i != course!![position].courseDate.size - 1) {
                                "$text${course!![position].courseDate[i].toDate().formatTo("yyyy/MM/dd HH:mm")}\n"
                            } else "$text${course!![position].courseDate[i].toDate().formatTo("yyyy/MM/dd HH:mm")}"
                        }
                    }
                    placeTextView.text = course!![position].coursePlace
                    applyTimeTextView.run {
                        text =
                            "第一階段：" + "${course!![position].firstApplyStartTime.toDate().formatTo("yyyy/MM/dd HH:mm")}~${course!![position].firstApplyEndTime.toDate().formatTo(
                                "yyyy/MM/dd HH:mm"
                            )}" + "\n" + "第二階段：" + "${course!![position].secondApplyStartTime.toDate().formatTo("yyyy/MM/dd HH:mm")}~${course!![position].secondApplyEndTime.toDate().formatTo(
                                "yyyy/MM/dd HH:mm"
                            )}"
                    }
                    costTextView.text =
                        if (course!![position].courseCost <= 0) "無" else course!![position].courseCost.toString()
                    applyMethodTextView.text = when (course!![position].applyMethod) {
                        0 -> "透過 push pull app"
                        else -> "其他"
                    }
                    enrollPostTextView.text = when (course!![position].enrollPost) {
                        0 -> "透過 push pull app"
                        else -> "其他"
                    }
                    otherTextView.text = if (course!![position].other == "") "無" else course!![position].other
                    my_course_item_apply_result_content.text =
                        if (course!![position].studentApply != 1) "尚未報名" else when (course!![position].applyResult) {
                            0 -> "正取"
                            1 -> "備取"
                            else -> "尚未報名"
                        }
                }
                checkFavorite.isChecked = course!![position].studentFavorite == 1
                checkFavorite.setOnCheckedChangeListener { _, isChecked ->
                    // Restore Edited Data
                    course!![position].studentFavorite = if (course!![position].studentFavorite == 1) 0 else 1

                    // Send Post Request to Server
                    setFavorite(isChecked, course!![position].id, course!![position].status < 3)
                }
                cellCheck.isChecked = course!![position].studentVote == 1

                // Setting of Buttons
                when (course!![position].status) {
                    0, 1, 2, 3, 4, 5, 6, 7 -> {
                        val twoButtonLayout: LinearLayout = findViewById(R.id.my_course_item_two_buttons)
                        twoButtonLayout.visibility = View.GONE

                        // Spare Button : Like listButton in Super Class
                        listButton.text = when (course!![position].status) {
                            0 -> "投票尚未開始"
                            1 -> if (course!![position].studentVote == 1) "取消投票" else "投票"
                            2 -> "投票已結束"
                            3 -> "尚未開始報名"
                            4, 6 -> if (course!![position].studentApply == 1) "取消報名" else "報名"
                            5, 7 -> "報名已截止"
                            else -> "Nothing"
                        }

                        // TODO : CHECK THIS
                        if (course!![position].status == 1 || course!![position].status == 4 || course!![position].status == 6) {
                            listButton.setOnClickListener {
                                if (course!![position].status == 1) {
                                    voteACourse(
                                        course!![position].studentVote == 1,
                                        course!![position].id,
                                        listButton,
                                        cellCheck,
                                        position,
                                        numOfApplyTextView,
                                        numOfApplyTextView,
                                        CourseModel.CoursePost(voteWeek),
                                        0
                                    )
                                } else if (course!![position].status == 4 && (course!![position].studentState == 1 || course!![position].studentState == 2)) {
                                    applyACourse(
                                        course!![position].studentApply == 1,
                                        course!![position].id,
                                        listButton,
                                        cellCheck,
                                        position,
                                        numOfApplyTextView,
                                        numOfApplyTextView,
                                        my_course_item_apply_result_content,
                                        0
                                    )
                                } else if (course!![position].status == 6 && course!![position].studentState != 3) {
                                    // Second-Stage Application
                                    applyACourse(
                                        course!![position].studentApply == 1,
                                        course!![position].id,
                                        listButton,
                                        cellCheck,
                                        position,
                                        numOfApplyTextView,
                                        numOfApplyTextView,
                                        my_course_item_apply_result_content,
                                        0
                                    )
                                }
                            }
                        } else {
                            listButton.disable()
                            cellCheck.isEnabled = false
                        }
                    }
                    8, 9, 10 -> {

                        // Make Unused Node GONE
                        listButton.visibility = View.GONE

                        cellCheck.isEnabled = false

                        // Setup of Left Button - Survey
                        // TODO : CHANGE JUDGEMENT
                        if (course!![position].satisfactionBool != 1) leftButton.disable()
                        else {
                            leftButton.setOnClickListener {
                                activity!!.supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.abc_fade_in,
                                        R.anim.abc_fade_out,
                                        R.anim.abc_fade_in,
                                        R.anim.abc_fade_out
                                    )
                                    .replace(R.id.push_pull_fragment_holder, SatisfactionSurveyFragment().also {
                                        it.arguments = Bundle().apply {

                                            // Satisfaction Survey Fragment Needs
                                            putInt("courseType", course!![position].courseType)
                                            putString("courseName", course!![position].courseName)
                                            putString("courseId", course!![position].id)
                                        }
                                    })
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                        // Setup of Right Button - Response
                        if (course!![position].status == 8) {
                            rightButton.disable()
                        } else {
                            rightButton.setOnClickListener {
                                activity!!.supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                        R.anim.abc_fade_in,
                                        R.anim.abc_fade_out,
                                        R.anim.abc_fade_in,
                                        R.anim.abc_fade_out
                                    )
                                    .replace(R.id.push_pull_fragment_holder, ResponseFragment().also {
                                        it.arguments = Bundle().apply {

                                            // Response Fragment Needs
                                            putString("courseId", course!![position].id)
                                        }
                                    })
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
                }

                cellCheck.text = when (course!![position].status) {
                    0, 1, 2 -> "投"
                    3, 4, 5, 6, 7 -> "報"
                    else -> "X"
                }

                if (course!![position].isExtended) {
                    upPart.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                    expandArrow.rotation = 180f
                } else {
                    upPart.layoutParams.height = resources.getDimension(R.dimen.courseRow_collapse_height).toInt()
                    expandArrow.rotation = 0f
                }

                // About Expanding and Collapsing
                expandArrow.setOnClickListener {

                    if (course!![position].isExtended) {
                        it.animate().setDuration(300).rotation(0f).start()
                        upPart.layoutParams.height = resources.getDimension(R.dimen.courseRow_collapse_height).toInt()
                    } else {
                        it.animate().setDuration(300).rotation(180f).start()
                        upPart.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
                    }
                    course!![position].isExtended = !course!![position].isExtended
                    // Make View Update
                    upPart.requestLayout()
                }
            }
        } else {
            // Other two cases can be just applied with Super Class
            super.getView(position, convertView, parent)
        }
    }
}