package com.example.cs.pushpull.personal.timetable

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.cs.pushpull.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class DayHasCourse(private val context: Context, private val list: List<String>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return list.contains("${day!!.year}-${day.month}-${day.day}")
    }

    override fun decorate(p0: DayViewFacade?) {
        p0!!.setSelectionDrawable(ContextCompat.getDrawable(context, R.drawable.calendar_has_course)!!)
    }
}

// TODO Color problem when current date is selected
class CurrentDate(private val context: Context) : DayViewDecorator {

    private val date = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?) = day!! == date

    override fun decorate(p0: DayViewFacade?) {

        p0!!.addSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
    }
}

class NotCurrentDate(private val context: Context) : DayViewDecorator {

    private val date = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?) = day!! == date

    override fun decorate(p0: DayViewFacade?) {

        p0!!.addSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
        )
    }

}
