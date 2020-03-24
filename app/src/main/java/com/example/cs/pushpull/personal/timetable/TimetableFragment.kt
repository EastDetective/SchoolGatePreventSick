package com.example.cs.pushpull.personal.timetable


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.now
import com.example.cs.pushpull.extension.toDate
import com.example.cs.pushpull.personal.PersonalApiService
import com.example.cs.pushpull.personal.model.TimeTableModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class TimetableFragment : Fragment() {

    private companion object {
        const val TAG = "TimetableFragment"
    }

    private lateinit var calendar: MaterialCalendarView
    private lateinit var classList: ListView
    private lateinit var month: TextView
    private lateinit var year: TextView
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private var dateYMD: List<String> = listOf()

    // Api Service for Course
    private val personalApiService by lazy {
        PersonalApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private var dateFull: List<String> = listOf()
    private var dateCourseFull: List<TimeTableModel.CourseDate> = listOf()

    private val decoratorHasCourse
        get() = DayHasCourse(context!!, dateYMD)
    private val decoratorIsCurrent
        get() = CurrentDate(context!!)
    private val decoratorIsNotCurrent
        get() = NotCurrentDate(context!!)

    @SuppressLint("SetTextI18n", "NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.school_timetables)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timetable, container, false).apply {

            calendar = findViewById(R.id.timetable_calendar)
            classList = findViewById(R.id.timetable_classList)
            month = findViewById(R.id.timetable_month_text)
            year = findViewById(R.id.timetable_year_text)
            btnPrev = findViewById(R.id.timetable_btn_prev)
            btnNext = findViewById(R.id.timetable_btn_next)

            val cal = Calendar.getInstance()
            val created =
                cal.get(Calendar.YEAR).toString() + "-" + (cal.get(Calendar.MONTH) + 1).toString() + "-" + cal.get(
                    Calendar.DAY_OF_MONTH
                ).toString()
            disposable =
                personalApiService.getDate((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        for (i in 0 until dateFull.size) {
                            val transform = dateFull[i].toDate("yyyy-MM-dd").formatTo("yyyy-M-d")
                            if (!dateYMD.contains(transform)) {
                                dateYMD = dateYMD.toMutableList().apply { add(transform) }.toList()
                            }
                            Log.d(TAG, transform)
                        }
                        getCourse(created)
                        calendar.addDecorators(decoratorHasCourse, decoratorIsCurrent)
                    }
                    .subscribeBy(
                        onNext = {
                            dateFull = it
                        },
                        onError = {
                            Log.e(TAG, it.message)
                        }
                    )

            setMonth(calendar.currentDate.month)
            year.text = calendar.currentDate.year.toString()

        }
    }

    @SuppressLint("SetTextI18n")
    fun getCourse(msg: String) {
        disposable =
            personalApiService.getDateCourse((activity as PushPull).studentUUID!!, msg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    val classType = ArrayList<Int>()
                    val className = ArrayList<String>()
                    val classTime = ArrayList<String>()
                    val adapter: TimeTableListAdapter?
                    for (j in 0 until dateCourseFull.size) {
                        classType.add(dateCourseFull[j].courseType)
                        className.add(dateCourseFull[j].courseName)
                        classTime.add(dateCourseFull[j].courseDate)
                    }
                    if (dateCourseFull.isEmpty()) {
                        classType.add(-1)
                        className.add("Nothing planned.")
                        classTime.add("")
                    }

                    adapter = TimeTableListAdapter(
                        context!!,
                        classType,
                        classTime,
                        className
                    )
                    classList.adapter = adapter
                }
                .subscribeBy(
                    onNext = {
                        dateCourseFull = it
                    },
                    onError = {
                        Log.e(TAG, it.message)
                    }
                )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar.run {

            selectedDate = CalendarDay.today()
            setWeekDayFormatter(ArrayWeekDayFormatter(arrayOf("MON", "TUE", "WED", "THR", "FRI", "SAT", "SUN")))

            topbarVisible = false
            setOnDateChangedListener { _, calendarDay, _ ->
                getCourse("${calendarDay.year}-${calendarDay.month}-${calendarDay.day}")
                calendar.addDecorator(DayHasCourse(context!!, dateYMD))
                if ("${calendarDay.year}-${calendarDay.month}-${calendarDay.day}" == now().formatTo("yyyy-M-dd")) {
                    removeDecorator(decoratorIsNotCurrent)
                    addDecorator(decoratorIsCurrent)
                } else {
                    removeDecorator(decoratorIsCurrent)
                    addDecorator(decoratorIsNotCurrent)
                }
               addDecorator(decoratorHasCourse)
            }
            setOnMonthChangedListener { _, calendarDay ->
                setMonth(calendarDay.month)
                year.text = calendarDay.year.toString()
            }
        }

        btnPrev.setOnClickListener { calendar.goToPrevious() }
        btnNext.setOnClickListener { calendar.goToNext() }
    }

    private fun setMonth(num: Int) {
        month.text = when (num) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            else -> "December"
        }
    }
}

