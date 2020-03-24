package com.example.cs.pushpull.school.previous

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.RectangleTextView
import com.example.cs.pushpull.extension.enable
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.SurveyModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException




class SatisfactionSurveyFragment : Fragment() {

    companion object {
        const val TAG = "Survey"
    }

    // UI Nodes
    private lateinit var courseTypeCirView: RectangleTextView
    private lateinit var courseNameTextView: TextView
    private lateinit var questionBody: RelativeLayout
    private lateinit var sendBtn: Button

    // Data Container (Default: Empty)
    private var questionInfo: SurveyModel.SurveyQuestion? = null
    private val questions: MutableList<Question> = mutableListOf()

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Add a Back Icon on ActionBar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title on the ActionBar
        activity?.title = resources.getString(R.string.satisfaction_survey)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_satisfaction_survey, container, false).apply {
            // Node Binding
            courseTypeCirView = findViewById(R.id.survey_circular_tv)
            courseNameTextView = findViewById(R.id.survey_course_name_text_view)
            questionBody = findViewById(R.id.survey_question_body)

            // Fill Basic Info
            courseNameTextView.text = arguments?.getString("courseName")
            when (arguments?.getInt("courseType")) {
                0 -> {
                    courseTypeCirView.run {
                        text = "語言"
                        setSolidColor(resources.getString(R.color.lightTeal))
                    }
                }
                1 -> {
                    courseTypeCirView.run {
                        text = "證照"
                        setSolidColor(resources.getString(R.color.yellowTan))
                    }
                }
                2 -> {
                    courseTypeCirView.run {
                        text = "其他"
                        setSolidColor(resources.getString(R.color.grayish))
                    }
                }
                else -> {
                    courseTypeCirView.run {
                        text = "Error"
                        setSolidColor(resources.getString(R.color.pink))
                    }
                }
            }
            arguments?.getString("courseId")?.run {
                getSurveyQuestion(this)
            } ?: run {
                Log.e(TAG, "CourseID is Null")
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun getSurveyQuestion(courseId: String) {
        disposable = courseApiService.getSatisfactionSurveyQuestion(courseId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                //questionInfo!!.kind.size
                questionInfo?.kind?.let {
                    for (i in 0 until questionInfo!!.kind.size) {
                        when (questionInfo!!.kind[i]) {
                            0 -> {
                                // Likert-Scale
                                Log.d("CORATEST","Likert-Scale")
                                questions.add(
                                    Question(
                                        i,
                                        questionInfo!!.quest[i],
                                        Pair(
                                            questionInfo!!.scale[i][1],
                                            questionInfo!!.scale[i][0]
                                        ),
                                        // First question doesn't need lastItemId
                                        if (i == 0) null else questions[i - 1].getLastItemId()
                                    )
                                )
                            }
                            1 -> {
                                // Short Description
                                questions.add(
                                    Question(
                                        i,
                                        questionInfo!!.quest[i],
                                        // First question doesn't need lastItemId
                                        if (i <= 0) null else questions[i - 1].getLastItemId()
                                    )
                                )
                            }
                            2 -> {
                                // multiple choice
                                questions.add(
                                    Question(
                                        i,
                                        questionInfo!!.quest[i],
                                        questionInfo!!.scale[i],
                                        // First question doesn't need lastItemId
                                        questionInfo!!.kind[i],
                                        if (i == 0) null else questions[i - 1].getLastItemId()
                                    )
                                )
                            }
                            3 -> {
                                // multiple choices
                                questions.add(
                                    Question(
                                        i,
                                        questionInfo!!.quest[i],
                                        questionInfo!!.scale[i],
                                        // First question doesn't need lastItemId
                                        if (i == 0) null else questions[i - 1].getLastItemId()
                                    )
                                )
                            }
                        }
                    }
                }

                // Force Button setOnclick after Data gotten
                sendBtn = view!!.findViewById(R.id.survey_send_btn)
                sendBtn.setOnClickListener {
                    var ans = ""
                    var string = ""
                    for (i in 0 until questions.size) {
                        string = questions[i].getAnswer().toString()
                        if (string.isEmpty() || string == "null") {
                            ans += if (i == 0) "第${i + 1}題" else ", 第${i + 1}題"
                        }
                    }
                    if (ans != "") {
                        ans += "未回答"
                        Toast.makeText(context, ans, Toast.LENGTH_LONG).show()
                    } else {
                        val answer: MutableList<String> = mutableListOf()
                        for (i in 0 until questions.size)
                            questions[i].getAnswer()?.let {
                                answer.add(questions[i].getAnswer()!!.toString())
                            }
                        answerSurvey(
                            SurveyModel.SurveyAnswer(
                                arguments?.getString("courseId")!!,
                                questionInfo!!.quest,
                                answer,
                                questionInfo!!.kind,
                                questionInfo!!.scale,
                                (activity as PushPull).studentUUID!!
                            )
                        )
                    }
                }

                questionInfo?.run {

                    // Do if Non-Null
                    sendBtn.enable()
                } ?: run {

                    // Do if Null
                    Toast.makeText(activity, "沒有可用問卷，請返回", Toast.LENGTH_LONG)
                }
            }
            .doOnError {
                // TODO : Test Function
                Log.d(TAG, "No Survey Available")
                onDestroy()
            }
            .subscribeBy(
                onNext = {
                    questionInfo = it
                },
                onComplete = {
                    Log.d(TAG, "Getting Question Complete!")
                },
                onError = {
                    Log.e(TAG, it.message)
                }
            )
    }

    @SuppressLint("ShowToast")
    fun answerSurvey(surveyAnswer: SurveyModel.SurveyAnswer) {
        // TODO : Finish
        disposable = courseApiService.answerSatisfactionSurvey(surveyAnswer)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onComplete = {
                    Log.d(TAG, "Send Complete!")
                    Toast.makeText(context, "問卷已提交", Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                },
                onError = {
                    if (it is HttpException) {
                        when (it.message!!.split(" ")[1].toInt()) {
                            601 -> {
                                Toast.makeText(
                                    context,
                                    resources.getString(R.string.error_code_E501),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    } else {
                        Log.e(TAG, it.message)
                    }
                }
            )
    }

    inner class Question(type: Int) {

        // Basic Attrs
        private var type: Int? = null
        private var scaleText: Pair<String, String>? = null
        private var text: List<String>? = null

        // UI Nodes
        lateinit var title: TextView
        private lateinit var quest: TextView
        private lateinit var scaleMax: TextView
        private lateinit var scaleMin: TextView
        private lateinit var tip: TextView
        private var radioScale: RadioGroup? = null
        private var radioButtons: List<RadioButton>? = null
        private var shortDescription: EditText? = null
        private var choose: MutableList<RadioButton> = mutableListOf()
        private var chooseScale: RadioGroup? = null
        private var check: MutableList<CheckBox> = mutableListOf()

        init {
            this.type = type
        }

        // Likert-Scale Question
        constructor(
            count: Int,
            questText: String,
            scaleText: Pair<String, String>,
            lastBottomId: Int? = null
        ) : this(0) {
            Log.d("CORATEST","Likert-Scale constructor")

            // Setup UI Nodes
            quest = TextView(context).apply {
                text = questText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                setTypeface(null, Typeface.BOLD)
                id = ViewCompat.generateViewId()
            }
            this.scaleText = scaleText
            scaleMax = TextView(context).apply {
                text = scaleText.first
                id = ViewCompat.generateViewId()
            }
            scaleMin = TextView(context).apply {
                text = scaleText.second
            }
            radioScale = RadioGroup(context).apply {
                setBackgroundResource(R.drawable.radio_btn_unchecked)
                orientation = RadioGroup.HORIZONTAL
                id = ViewCompat.generateViewId()
                Log.d("CORATEST","radioScale")

                radioButtons = listOf(
                    RadioButton(context).apply { text = "1" },
                    RadioButton(context).apply { text = "2" },
                    RadioButton(context).apply { text = "3" },
                    RadioButton(context).apply { text = "4" },
                    RadioButton(context).apply { text = "5" }
                ).onEach {
                    it.id = ViewCompat.generateViewId()
                    it.setBackgroundResource(R.drawable.radio_button)
                    it.setButtonDrawable(android.R.color.transparent)
                    it.gravity = Gravity.CENTER
                }

                val displayMetrics = DisplayMetrics()
                activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                var width = (displayMetrics.widthPixels/ (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT) - 80f)/5


                for (i in radioButtons!!) addView(
                    i, LinearLayout.LayoutParams(
                        width.toInt().dp,
                        35.dp,
                        1.0f
                    )
                )
            }

            // Add UI Nodes to Layout
            questionBody.run {
                addView(
                    quest, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        if (count == 0) {
                            it.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        } else {
                            it.addRule(RelativeLayout.BELOW, lastBottomId!!)
                        }
                        it.topMargin = 20.dp
                    }
                )
                addView(
                    scaleMax, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.addRule(RelativeLayout.BELOW, quest.id)
                        it.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                        it.topMargin = 20.dp
                    }
                )
                addView(
                    scaleMin, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.addRule(RelativeLayout.BELOW, quest.id)
                        it.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                        it.topMargin = 20.dp
                    }
                )
                addView(
                    radioScale, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.addRule(RelativeLayout.BELOW, scaleMax.id)
                        it.topMargin = 15.dp
                    }
                )
            }
        }

        // Short Description Question
        constructor(count: Int, questText: String, lastBottomId: Int? = null) : this(1) {

            // Setup UI Nodes
            quest = TextView(context).apply {
                text = questText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                setTypeface(null, Typeface.BOLD)
                id = ViewCompat.generateViewId()
            }
            shortDescription = EditText(context).apply {
                hint = "回答問題限150字以內。"
                filters = arrayOf(InputFilter.LengthFilter(150))
                inputType = InputType.TYPE_CLASS_TEXT
                setSingleLine(false)
                setBackgroundResource(R.drawable.input_edit_round)
                gravity = Gravity.TOP or Gravity.START
                minHeight = 200.dp
                id = ViewCompat.generateViewId()
            }

            // Add UI Nodes to Layout
            questionBody.run {
                addView(
                    quest, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        if (count == 0) {
                            it.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        } else {
                            it.addRule(RelativeLayout.BELOW, lastBottomId!!)
                        }
                        it.topMargin = 20.dp
                    }
                )
                addView(
                    shortDescription, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.topMargin = 15.dp
                        it.addRule(RelativeLayout.BELOW, quest.id)
                    }
                )
            }
        }

        // multiple choice Question
        constructor(
            count: Int,
            questText: String,
            ListText: List<String>,
            kind: Int,
            lastBottomId: Int? = null
        ) : this(2) {
            // Setup UI Nodes
            quest = TextView(context).apply {
                text = questText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                setTypeface(null, Typeface.BOLD)
                id = ViewCompat.generateViewId()
            }
            this.text = ListText
            chooseScale = RadioGroup(context).apply {
                //setBackgroundResource(R.drawable.radio_btn_unchecked)
                orientation = RadioGroup.VERTICAL
                gravity = Gravity.CENTER
                id = ViewCompat.generateViewId()
                for (i in 0 until ListText.size) {
                    choose.add(RadioButton(context).apply { text = ListText[i] })
                }
                for (i in 0 until ListText.size) {
                    choose[i].id = i
                }
                choose.onEach {
                    //it.id = ViewCompat.generateViewId()
                    it.setBackgroundResource(R.drawable.radio_button)
                    it.setButtonDrawable(android.R.color.transparent)
                    it.gravity = Gravity.CENTER
                    it.paddingBottom
                }

                for (i in choose) addView(
                    i, LinearLayout.LayoutParams(
                        300.dp,
                        45.dp
                    ).also {
                        it.topMargin = 10.dp
                    }
                )
            }
            // Add UI Nodes to Layout
            questionBody.run {
                addView(
                    quest, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        if (count == 0) {
                            it.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        } else {
                            it.addRule(RelativeLayout.BELOW, lastBottomId!!)
                        }
                        it.topMargin = 20.dp
                    }
                )
                addView(
                    chooseScale, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    ).also {
                        it.addRule(RelativeLayout.BELOW, quest.id)
                        it.addRule(RelativeLayout.CENTER_IN_PARENT)
                        it.topMargin = 15.dp
                    }
                )
            }
        }

        // multiple choices Question
        constructor(
            count: Int,
            questText: String,
            ListText: List<String>,
            lastBottomId: Int? = null
        ) : this(3) {
            // Setup UI Nodes
            quest = TextView(context).apply {
                text = questText
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                setTypeface(null, Typeface.BOLD)
                id = ViewCompat.generateViewId()
            }
            tip = TextView(context).apply {
                text = "*可多選"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                id = ViewCompat.generateViewId()
            }
            this.text = ListText
            chooseScale = RadioGroup(context).apply {
                //setBackgroundResource(R.drawable.radio_btn_unchecked)
                orientation = RadioGroup.VERTICAL
                gravity = Gravity.CENTER
                id = ViewCompat.generateViewId()
                for (i in 0 until ListText.size) {
                    check.add(CheckBox(context).apply { text = ListText[i] })
                }
                for (i in 0 until ListText.size) {
                    check[i].id = i
                }
                check.onEach {
                    //it.id = ViewCompat.generateViewId()
                    it.setBackgroundResource(R.drawable.radio_button)
                    it.setButtonDrawable(android.R.color.transparent)
                    it.gravity = Gravity.CENTER
                }

                for (i in check) addView(
                    i, LinearLayout.LayoutParams(
                        300.dp,
                        45.dp
                    ).also {
                        it.topMargin = 10.dp
                    }
                )
            }
            // Add UI Nodes to Layout
            questionBody.run {
                addView(
                    quest, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        if (count == 0) {
                            it.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        } else {
                            it.addRule(RelativeLayout.BELOW, lastBottomId!!)
                        }
                        it.topMargin = 20.dp
                    }
                )
                addView(
                    tip, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.addRule(RelativeLayout.BELOW, quest.id)
                        it.addRule(RelativeLayout.ALIGN_LEFT)
                        it.topMargin = 5.dp
                    }
                )
                addView(
                    chooseScale, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    ).also {
                        it.addRule(RelativeLayout.BELOW, tip.id)
                        it.addRule(RelativeLayout.CENTER_IN_PARENT)
                        it.topMargin = 20.dp
                    }
                )
            }
        }

        fun getAnswer(): String? {
            when (type) {
                0 -> {
                    for (i in radioButtons!!) {
                        if (i.isChecked) return i.text.toString()
                    }
                }
                1 -> {
                    return shortDescription!!.text.toString()
                }
                2 -> {
                    for (i in choose) {
                        if (i.isChecked) {
                            return (i.id).toString()
                        }
                    }
                }
                3 -> {
                    var all = ""
                    for (i in check) {
                        if (i.isChecked) {
                            all += (i.id).toString() + ","
                        }
                    }
                    return all
                }
            }
            return null
        }

        // Get ID of Last Node for setting Margin of Next Question
        fun getLastItemId(): Int {
            return when (type) {
                0 -> {
                    radioScale!!.id
                }
                1 -> {
                    shortDescription!!.id
                }
                2 -> {
                    chooseScale!!.id
                }
                3 -> {
                    chooseScale!!.id
                }
                else -> {
                    -1
                }
            }
        }

        // Extension Function for Int to DiP
        private val Int.dp: Int
            get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}

