package com.example.cs.pushpull.school.allcourse

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.*
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.CourseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_all_course.view.*
import retrofit2.HttpException

open class AllCourseListAdapter(context: Context, courseList: List<CourseModel.Course>) : BaseAdapter() {

    private companion object {
        const val TAG = "AllCourseList"
    }

    // Display Data (Processed)
    internal var course: List<CourseModel.Course>? = courseList
    private var applyGet: CourseModel.Apply? = null
    private var studentId: String? = null

    // UI Usage
    internal var inflater: LayoutInflater? = null
    private var isVote = false

    // WebApiService Needed
    private lateinit var courseApiService: CourseApiService
    private var disposable: Disposable? = null

    constructor(
        context: Context,
        courseList: List<CourseModel.Course>,
        courseApiService: CourseApiService,
        disposable: Disposable?,
        studentId: String,
        isVote: Boolean = false
    ) : this(context, courseList) {

        // Distinguish Usage
        if (isVote) this.isVote = isVote

        this.studentId = studentId
        this.courseApiService = courseApiService
        this.disposable = disposable
    }

    init {
        course = courseList
        inflater = LayoutInflater.from(context)
    }

    // Base Member Function Needed
    override fun getItem(position: Int) = course!![position]

    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = course!!.size
    @SuppressLint("ViewHolder", "NewApi", "ResourceType", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?) =
        inflater!!.inflate(R.layout.item_all_course, parent, false).apply {

            // Binding Nodes and filling text in
            val circularTextView: RectangleTextView = findViewById(R.id.course_item_circular_text_view)
            val nameTextView: TextView = findViewById(R.id.course_item_name)
            val numOfApplyTextView: TextView = findViewById(R.id.course_item_num_student)
            val numOfApplyTextView2: TextView = findViewById(R.id.course_item_num_student2)
            numOfApplyTextView2.visibility = View.GONE
            val stateTextView: TextView = findViewById(R.id.course_item_state)
            val introTextView: TextView = findViewById(R.id.course_item_intro)
            val teacherTextView: TextView = findViewById(R.id.course_item_course_teacher_content)
            val targetStdTextView: TextView = findViewById(R.id.course_item_target_student_content)
            val targetStdTitle: TextView = findViewById(R.id.course_item_target_student_title)
            val numOfStdTextView: TextView = findViewById(R.id.course_item_num_of_student_content)
            val campusTextView: TextView = findViewById(R.id.course_item_campus_content)
            val dateTextView: TextView = findViewById(R.id.course_item_course_date_content)
            val placeTextView: TextView = findViewById(R.id.course_item_course_place_content)
            val placeTitle: TextView = findViewById(R.id.course_item_course_place_title)
            val applyTimeTextView: TextView = findViewById(R.id.course_item_apply_date_content)
            val costTextView: TextView = findViewById(R.id.course_item_course_cost_content)
            val applyMethodTextView: TextView = findViewById(R.id.course_item_apply_method_content)
            val enrollPostTextView: TextView = findViewById(R.id.course_item_enroll_post_content)
            val otherTextView: TextView = findViewById(R.id.course_item_other_content)
            val checkFavorite: CheckBox = findViewById(R.id.course_item_check_favorite)
            val voteTitle: TextView = findViewById(R.id.course_item_vote_time_title)
            voteTitle.visibility = View.GONE
            val voteTextView: TextView = findViewById(R.id.course_item_vote_time)
            voteTextView.visibility = View.GONE
            val hopeVoteTitle: TextView = findViewById(R.id.course_item_hopeVote_day_title)
            val hopeVoteTextView: TextView = findViewById(R.id.course_item_hopeVote_day)
            hopeVoteTitle.visibility = View.GONE
            hopeVoteTextView.visibility = View.GONE
            val listButton: Button = findViewById(R.id.course_item_button)
//            listButton.visibility = View.GONE
            val cellCheck: CheckBox = findViewById(R.id.course_item_checkbox_btn)
            val expandArrow: ImageView = findViewById(R.id.course_item_arrow)
            val upPart: RelativeLayout = findViewById(R.id.course_item_up_part_layout)
            val chooseDay: LinearLayout = findViewById(R.id.choose_day)
            chooseDay.visibility = View.GONE
            val chooseMon: CheckBox = findViewById(R.id.mon)
            val chooseTue: CheckBox = findViewById(R.id.tue)
            val chooseWed: CheckBox = findViewById(R.id.wed)
            val chooseThu: CheckBox = findViewById(R.id.thu)
            val chooseFri: CheckBox = findViewById(R.id.fri)
            val chooseSat: CheckBox = findViewById(R.id.sat)
            val chooseSun: CheckBox = findViewById(R.id.sun)
            val voteWeek: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0)

            if (course!![position].status in 14..15) {
                chooseMon.isClickable = false
                chooseTue.isClickable = false
                chooseWed.isClickable = false
                chooseThu.isClickable = false
                chooseFri.isClickable = false
                chooseSat.isClickable = false
                chooseSun.isClickable = false
            }

            val displayMetrics = resources.displayMetrics
            var width = (displayMetrics.widthPixels/ (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT) - 80f)/5
            chooseMon.width = width.toInt()
            chooseTue.width = width.toInt()
            chooseWed.width = width.toInt()
            chooseThu.width = width.toInt()
            chooseFri.width = width.toInt()
            chooseSat.width = width.toInt()
            chooseSun.width = width.toInt()

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
//                9, 10 -> "${course!![position].numberOfApply}" + "人上課"
                else -> ""
            }
            numOfApplyTextView2.text = when (course!![position].status) {
                0, 1, 2 -> "${course!![position].numberOfVoters}" + "人已投票"
                3, 4, 5, 6, 7, 8, 9, 10 -> "${course!![position].numberOfApply}" + "人報名中"
//                9, 10 -> "${course!![position].numberOfApply}" + "人上課"
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
            if (course!![position].status in 0..2 || course!![position].status in 11..15) {
                campusTextView.text = if (course!![position].campus == 0) "博愛校區" else "天母校區"
                val numOfStdTitle: TextView = findViewById(R.id.course_item_num_of_student_title)
                numOfStdTitle.visibility = View.GONE
                numOfStdTextView.visibility = View.GONE
                val teacherTitle: TextView = findViewById(R.id.course_item_course_teacher_title)
                teacherTitle.visibility = View.GONE
                teacherTextView.visibility = View.GONE
                val dateTitle: TextView = findViewById(R.id.course_item_course_date_title)
                dateTitle.visibility = View.GONE
                dateTextView.visibility = View.GONE
                val voteTitle2: TextView = findViewById(R.id.course_item_vote_time_title)
                val voteTextView2: TextView = findViewById(R.id.course_item_vote_time)
                val hopeVoteTitle2: TextView = findViewById(R.id.course_item_hopeVote_day_title)
                val hopeVoteTextView2: TextView = findViewById(R.id.course_item_hopeVote_day)
                val applyTimeTitle: TextView = findViewById(R.id.course_item_apply_date_title)
                applyTimeTitle.visibility = View.GONE
                applyTimeTextView.visibility = View.GONE
                val costTitle: TextView = findViewById(R.id.course_item_course_cost_title)
                costTitle.visibility = View.GONE
                costTextView.visibility = View.GONE
                val applyMethodTitle: TextView = findViewById(R.id.course_item_apply_method_title)
                applyMethodTitle.visibility = View.GONE
                applyMethodTextView.visibility = View.GONE
                val enrollPostTitle: TextView = findViewById(R.id.course_item_enroll_post_title)
                enrollPostTitle.visibility = View.GONE
                enrollPostTextView.visibility = View.GONE
                numOfApplyTextView2.visibility = View.VISIBLE
                val otherTitle: TextView = findViewById(R.id.course_item_other_title)
                otherTitle.visibility = View.GONE
                otherTextView.visibility = View.GONE
                course_item_apply_result_title.visibility = View.GONE
                course_item_apply_result_content.visibility = View.GONE
                placeTextView.visibility = View.GONE
                placeTitle.visibility = View.GONE
                targetStdTextView.visibility = View.GONE
                targetStdTitle.visibility = View.GONE

                if (course!![position].status !in 11..13) {
                    chooseDay.visibility = View.VISIBLE
                    voteTitle2.visibility = View.VISIBLE
                    voteTextView2.visibility = View.VISIBLE
                    hopeVoteTitle2.visibility = View.VISIBLE
                    hopeVoteTextView2.visibility = View.VISIBLE
                } else {
                    listButton.visibility = View.GONE
                }

                voteTextView2.text =
                    "${course!![position].voteStartTime.toDate().formatTo("yyyy/MM/dd HH:mm")}~\n${course!![position].voteEndTime.toDate().formatTo(
                        "yyyy/MM/dd HH:mm"
                    )}"
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
                        "第一階段：\n" + "${course!![position].firstApplyStartTime.toDate().formatTo("yyyy/MM/dd HH:mm")}~\n${course!![position].firstApplyEndTime.toDate().formatTo(
                            "yyyy/MM/dd HH:mm"
                        )}" + "\n" + "第二階段：\n" + "${course!![position].secondApplyStartTime.toDate().formatTo("yyyy/MM/dd HH:mm")}~\n${course!![position].secondApplyEndTime.toDate().formatTo(
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
                if (course!![position].status in 8..10) {
                    course_item_apply_result_title.visibility = View.GONE
                    course_item_apply_result_content.visibility = View.GONE
                }
                course_item_apply_result_content.text =
                    if (course!![position].studentApply != 1) "尚未報名" else when (course!![position].applyResult) {
                        0 -> "正取"
                        1 -> "備取"
                        else -> "尚未報名"
                    }
            }

            // Listener of CheckBox
            checkFavorite.isChecked = course!![position].studentFavorite == 1
            checkFavorite.setOnCheckedChangeListener { _, isChecked ->
                // Restore Edited Data
                course!![position].studentFavorite = if (course!![position].studentFavorite == 1) 0 else 1

                // Send Post Request to Server
                setFavorite(isChecked, course!![position].id, course!![position].status < 3)
            }

            cellCheck.isChecked = course!![position].studentVote == 1

            // Setting of Button
            listButton.text = when (course!![position].status) {
                0 -> "投票尚未開始"
                1 -> if (course!![position].studentVote == 1) "取消投票" else "投票"
                2 -> "投票已結束"
                3 -> "尚未開始報名"
                4 -> if (course!![position].studentState == 0 || course!![position].studentState == 3) "限第一階段報名" else if (course!![position].studentApply == 1) "取消報名" else "報名"
                6 -> if (course!![position].studentApply == 1) "取消報名" else "報名"
                5, 7 -> "報名已截止"
                14, 15 -> "投票已截止"
                else -> "Nothing"
            }
            cellCheck.text = when (course!![position].status) {
                0, 1, 2 -> "投"
                3, 4, 5, 6, 7 -> "報"
                else -> "報"
            }
            if (course!![position].status > 7) {
                listButton.visibility = View.GONE
            }
            if (course!![position].status != 1 && course!![position].status != 4 && course!![position].status != 6) {
                // if the button shouldn't be pressed
                listButton.disable()
                cellCheck.isEnabled = false
            } else if (course!![position].status == 4 && (course!![position].studentState == 0 || course!![position].studentState == 3)) {
                // TODO : Optimize the code
                listButton.disable()
                cellCheck.isEnabled = false
            } else {
                chooseMon.setOnClickListener {
                    if (chooseMon.isChecked) {
                        voteWeek[0] = 1
                    } else
                        voteWeek[0] = 0
                }
                chooseTue.setOnClickListener {
                    if (chooseTue.isChecked) {
                        voteWeek[1] = 1
                    } else
                        voteWeek[1] = 0
                }
                chooseWed.setOnClickListener {
                    if (chooseWed.isChecked) {
                        voteWeek[2] = 1
                    } else
                        voteWeek[2] = 0
                }
                chooseThu.setOnClickListener {
                    if (chooseThu.isChecked) {
                        voteWeek[3] = 1
                    } else
                        voteWeek[3] = 0
                }
                chooseFri.setOnClickListener {
                    if (chooseFri.isChecked) {
                        voteWeek[4] = 1
                    } else
                        voteWeek[4] = 0
                }
                chooseSat.setOnClickListener {
                    if (chooseSat.isChecked) {
                        voteWeek[5] = 1
                    } else
                        voteWeek[5] = 0
                }
                chooseSun.setOnClickListener {
                    if (chooseSun.isChecked) {
                        voteWeek[6] = 1
                    } else
                        voteWeek[6] = 0
                }
                listButton.setOnClickListener {
                    if (course!![position].studentVote == 1) {
                        cellCheck.isChecked = false
                    }
                    if (course!![position].status == 1) {
                        // Vote
                        voteACourse(
                            course!![position].studentVote == 1,
                            course!![position].id,
                            listButton,
                            cellCheck,
                            position,
                            numOfApplyTextView,
                            numOfApplyTextView2,
                            CourseModel.CoursePost(voteWeek),
                            0
                        )
                    } else if (course!![position].status == 4 && (course!![position].studentState == 1 || course!![position].studentState == 2)) {
                        // First-Stage Application
                        applyACourse(
                            course!![position].studentApply == 1,
                            course!![position].id,
                            listButton,
                            cellCheck,
                            position,
                            numOfApplyTextView,
                            numOfApplyTextView2,
                            course_item_apply_result_content,
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
                            numOfApplyTextView2,
                            course_item_apply_result_content,
                            0
                        )
                    }
                }
                cellCheck.setOnClickListener {
                    if (course!![position].status == 1) {
                        // Vote
                        voteACourse(
                            course!![position].studentVote == 1,
                            course!![position].id,
                            listButton,
                            cellCheck,
                            position,
                            numOfApplyTextView,
                            numOfApplyTextView2,
                            CourseModel.CoursePost(voteWeek),
                            1
                        )
                    } else if (course!![position].status == 4 && (course!![position].studentState == 1 || course!![position].studentState == 2)) {
                        // First-Stage Application
                        applyACourse(
                            course!![position].studentApply == 1,
                            course!![position].id,
                            listButton,
                            cellCheck,
                            position,
                            numOfApplyTextView,
                            numOfApplyTextView2,
                            course_item_apply_result_content,
                            1
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
                            numOfApplyTextView2,
                            course_item_apply_result_content,
                            1
                        )
                    }
                }
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
                upPart.requestLayout()
            }
        }!!

    fun setFavorite(toDo: Boolean, courseId: String, isOnVoted: Boolean = false) {
        if (isOnVoted) {
            if (toDo) {
                disposable = courseApiService.setRecFavorite(studentId!!, courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onComplete = {
                            Log.d(TAG, "Set As Favorite Complete")
                        },
                        onError = {
                            Log.d(TAG, it.message)
                        }
                    )
            } else {
                disposable = courseApiService.setRecNonFavorite(studentId!!, courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onComplete = {
                            Log.d(TAG, "Remove from Favorite Complete")
                        },
                        onError = {
                            Log.d(TAG, it.message)
                        }
                    )
            }
        } else {
            if (toDo) {
                disposable = courseApiService.setFavorite(studentId!!, courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onComplete = {
                            Log.d(TAG, "Set As Favorite Complete")
                        },
                        onError = {
                            Log.d(TAG, it.message)
                        }
                    )
            } else {
                disposable = courseApiService.setNonFavorite(studentId!!, courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onComplete = {
                            Log.d(TAG, "Remove from Favorite Complete")
                        },
                        onError = {
                            Log.d(TAG, it.message)
                        }
                    )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun voteACourse(
        isVoted: Boolean,
        courseId: String,
        btn: Button,
        checkBtn: CheckBox,
        position: Int,
        textView: TextView,
        textView2: TextView?,
        courseWeek: CourseModel.CoursePost,
        n: Int
    ) {
        if (!isVoted) {
            disposable = courseApiService.voteCourse(studentId!!, courseId, courseWeek)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    // UI Change
                    if (n == 0) {
                        btn.text = "取消投票"
                        checkBtn.isChecked = true
                    } else if (n == 1) {
                        checkBtn.text = "投"
                        btn.text = "取消投票"
                    }
                    course!![position].apply {
                        studentVote = 1
                        numberOfVoters++
                    }
                    textView.text = "${course!![position].numberOfVoters}" + "人已投票"
                    textView2?.text = "${course!![position].numberOfVoters}" + "人已投票"
                }
                .subscribeBy(
                    onComplete = {
                        Log.d(TAG, "Vote Succeed!")
                    },
                    onError = {
                        Log.d(TAG, it.message)
                    }
                )
        } else {
            disposable = courseApiService.disVoteCourse(studentId!!, courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    // UI Change
                    if (n == 0) {
                        btn.text = "投票"
                        checkBtn.isChecked = false
                    } else if (n == 1) {
                        checkBtn.text = "投"
                        btn.text = "投票"
                    }
                    course!![position].apply {
                        studentVote = 0
                        numberOfVoters--
                    }
                    textView.text = "${course!![position].numberOfVoters}" + "人已投票"
                    textView2?.text = "${course!![position].numberOfVoters}" + "人已投票"
                }
                .subscribeBy(
                    onComplete = {
                        Log.d(TAG, "DisVote Succeed!")
                    },
                    onError = {
                        Log.d(TAG, it.message)
                    }
                )
        }
    }

    @SuppressLint("SetTextI18n")
    fun applyACourse(
        isApplied: Boolean,
        courseId: String,
        btn: Button,
        checkBtn: CheckBox,
        position: Int,
        textView: TextView,
        textView2: TextView?,
        textView3: TextView?,
        n: Int
    ) {
        if (!isApplied) {
            disposable = courseApiService.applyCourse(studentId!!, courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    // UI Change
                    if (n == 0) {
                        btn.text = "取消報名"
                        checkBtn.isChecked = true
                    } else if (n == 1) {
                        checkBtn.text = "報"
                        btn.text = "取消報名"
                    }
                    course!![position].apply {
                        studentApply = 1
                        numberOfApply++
                    }
                    textView.text = "${course!![position].numberOfApply}" + "人報名中"
                    textView2?.text = "${course!![position].numberOfApply}" + "人報名中"
                    textView3?.text =
                        when (applyGet?.state) {
                            0 -> "正取"
                            1 -> "備取"
                            else -> "尚未報名"
                        }
                }
                .subscribeBy(
                    onNext = {
                        applyGet = it
                    },
                    onComplete = {
                        Log.d(TAG, "Apply Succeed!")
                    },
                    onError = {
                        if (it is HttpException) {
                            when (it.message!!.split(" ")[1].toInt()) {
                                606 -> {
                                    Toast.makeText(inflater!!.context, "第一階段已額滿", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Log.d(TAG, it.message)
                        }
                    }
                )
        } else {
            disposable = courseApiService.disApplyCourse(studentId!!, courseId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    // UI Change
                    if (n == 0) {
                        btn.text = "報名"
                        checkBtn.isChecked = false
                    } else if (n == 1) {
                        checkBtn.text = "報"
                        btn.text = "報名"
                    }
                    course!![position].apply {
                        studentApply = 0
                        numberOfApply--
                    }
                    textView.text = "${course!![position].numberOfApply}" + "人報名中"
                    textView2?.text = "${course!![position].numberOfApply}" + "人報名中"
                    textView3?.text = "尚未報名"
                }
                .subscribeBy(
                    onComplete = {
                        Log.d(TAG, "DisApply Succeed!")
                    },
                    onError = {
                        Log.d(TAG, it.message)
                    }
                )
        }
    }
}