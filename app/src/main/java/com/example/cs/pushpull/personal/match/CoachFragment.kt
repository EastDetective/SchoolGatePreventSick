package com.example.cs.pushpull.personal.match

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.R

class CoachFragment : Fragment() {

    companion object {
        const val TAG = "button"
    }

    private lateinit var timeTablelayout: TableLayout
    private lateinit var sendBtn: Button
    private lateinit var choseCompulsory: RadioButton
    private lateinit var choseMustSelect: RadioButton
    private lateinit var choseTogetherCompulsory: RadioButton
    private lateinit var choseProgramming: RadioButton
    private lateinit var inputCoachedSubject: EditText
    private lateinit var inputCoachedRequest: EditText
    private lateinit var classRadioGroup: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_match_coach, container, false).apply {
            timeTablelayout = findViewById(R.id.select_coached_allow_time)
            sendBtn = findViewById(R.id.coached_btn_send)
//            choseCompulsory = findViewById(R.id.coached_select_compulsory)
//            choseMustSelect = findViewById(R.id.coached_select_must_selected)
//            choseTogetherCompulsory = findViewById(R.id.coached_select_together)
//            choseProgramming = findViewById(R.id.coached_select_program)
            inputCoachedSubject = findViewById(R.id.text_input_subject)
            inputCoachedRequest = findViewById(R.id.input_coached_request)
            classRadioGroup = findViewById(R.id.coached_radio_group)

            createTable(3, 8)

            classRadioGroup.setOnCheckedChangeListener { classRadioGroup, checkedId ->
                if (checkedId != -1) {
                    // No item selected
                    Toast.makeText(context, "尚未選擇課程類別", Toast.LENGTH_LONG).show()
                } else {
                    val coachedClassString = resources.getResourceEntryName(checkedId)

                }

            }

            //改成function
            sendBtn.setOnClickListener {
                CheckisNotEmpty()
            }


        }
    }

    val aWeekPeriod = resources.getStringArray(R.array.coached_week_select)
    val aDayPeriod = resources.getStringArray(R.array.coached_time_select)
    //TODO button tag
    val allowTimePeriod = resources.getStringArray(R.array.coached_time_final)
    val checkedId = classRadioGroup.checkedRadioButtonId
    val coachedSubject = inputCoachedSubject.text.toString().trim()
    val coachedRequest = inputCoachedRequest.text.toString().trim()
    val ft = fragmentManager!!.beginTransaction()

    private fun createTable(rows: Int, cols: Int) {
        val row = TableRow(context)
        val button = Button(context)
        val timeOfaWeek = TextView(context)
        for (i in 0 until rows) {
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            for (j in 0 until cols) {
                if (i == 0 && j == 0) {
                    timeOfaWeek.apply {
                        layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                    }
                    timeOfaWeek.setText(" ")
                } else if (i == 0) {
                    timeOfaWeek.apply {
                        layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                    }
                    timeOfaWeek.setText(aWeekPeriod[j - 1])
                } else if (j == 0) {
                    button.apply {
                        layoutParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                    }
                    timeOfaWeek.setText(aDayPeriod[i - 1])
                    //TODO
                    button.setTag(allowTimePeriod[i - 1][j - 1])
                }
                row.addView(timeOfaWeek)
                row.addView(button)
                button.setOnClickListener {
                    //TODO
                    Log.d(TAG, button.getTag() as String?)
                }

            }
            timeTablelayout.addView(row)
        }

    }

    private fun CheckisNotEmpty() {
        if (coachedSubject.isNotEmpty() && coachedRequest.isNotEmpty() && checkedId != -1) {
            gotoNextPage()
        } else {
            showDialog()
        }
    }

    private fun gotoNextPage() {
        ft.replace(R.id.match_submit_fragment, SubmitFragment())
        ft.commit()
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("資料填寫不完整")
        builder.setMessage("請將資料填寫完整")
        builder.setPositiveButton("確認"){
                dialog, which -> dialog.cancel()
        }
        builder.show()
    }

}

