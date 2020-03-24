package com.example.cs.pushpull.school.leave

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate
import com.example.cs.pushpull.school.model.TakeLeaveModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import kotlin.collections.ArrayList

class TakeLeaveFragment : Fragment() {

    companion object {
        const val TAG = "TakeLeaveContent"
    }

    private lateinit var numberPickers: NumberPicker
    private lateinit var choseClassTextView: TextView
    private lateinit var choseClassBtn: Button
    private lateinit var choseDateTextView: TextView
    private lateinit var choseDateBtn: Button
    private lateinit var radioSick: RadioButton
    private lateinit var radioSomething: RadioButton
    private lateinit var radioPublic: RadioButton
    private lateinit var radioOther: RadioButton
    private lateinit var reasonTakeLeaveEditText: EditText
    private lateinit var sendTakeLeaveBtn: Button

    private lateinit var date: String
    private var courseNameArray = emptyArray<String>()
    private var dateFull: TakeLeaveModel.TakeLeaveDate? = null
    private var tempDate: ArrayList<String> = arrayListOf()

    private val takeLeaveApiService by lazy {
        TakeLeaveApiService.create()
    }
    private var disposable: Disposable? = null
    private var courseNameMap: MutableMap<String, String> = mutableMapOf()

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.title = resources.getString(R.string.take_leave)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val thisView = inflater.inflate(R.layout.fragment_take_leave, container, false)

        choseClassTextView = thisView.findViewById(R.id.choseClassTextView)
        choseClassBtn = thisView.findViewById(R.id.choseClassBtn)
        choseDateTextView = thisView.findViewById(R.id.choseDateTextView)
        choseDateBtn = thisView.findViewById(R.id.choseDateBtn)
        radioSick = thisView.findViewById(R.id.takeLeave_sickRadio)
        radioSomething = thisView.findViewById(R.id.takeLeave_somethingRadio)
        radioPublic = thisView.findViewById(R.id.takeLeave_publicRadio)
        radioOther = thisView.findViewById(R.id.takeLeave_otherRadio)
        reasonTakeLeaveEditText = thisView.findViewById(R.id.textTakeLeave)
        sendTakeLeaveBtn = thisView.findViewById(R.id.sendTakeLeaveBtn)

        choseClassBtn.visibility = View.GONE
        choseClassTextView.visibility = View.GONE

        choseClassBtn.isClickable = false

        // Get Courses which User can take leave
        disposable = takeLeaveApiService.getTakeLeave((activity as PushPull).studentUUID!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                choseClassBtn.visibility = View.VISIBLE
                choseClassTextView.visibility = View.VISIBLE
            }
            .subscribeBy(
                onNext = { leaveCourse ->

                    if (leaveCourse.isEmpty()) {
                        choseClassTextView.text = "當前無課程"
                        sendTakeLeaveBtn.setOnClickListener {
                            Toast.makeText(context, "目前無上課中之課程", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        for (i in 0 until leaveCourse.size) {
                            courseNameArray += leaveCourse[i].courseName
                            courseNameMap[leaveCourse[i].courseName] = leaveCourse[i].id
                        }

                        choseClassBtn.isClickable = true
                        choseClassBtn.setOnClickListener {
                            androidx.appcompat.app.AlertDialog.Builder(context!!).apply {
                                setTitle("Choose a Course.")
                                setItems(courseNameArray) { _, whichCourse ->

                                    choseClassTextView.text = courseNameArray[whichCourse]
                                    choseClassBtn.setBackgroundResource(R.drawable.yellow)

                                    choseDateTextView.text = "請選擇課程"
                                    choseDateBtn.setBackgroundResource(R.drawable.roll_call_select_gray)

                                    disposable =
                                        takeLeaveApiService.getTakeLeaveDate(courseNameMap[choseClassTextView.text.toString()].toString())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doOnTerminate {

                                                choseDateBtn.setOnClickListener {
                                                    for (i in 0 until dateFull?.courseDate?.size!!) {
                                                        if (i.rem(2) == 0) {
                                                            tempDate.add(dateFull?.courseDate!![i].toDate().formatTo("YYYY-MM-dd"))
                                                        }
                                                    }
                                                    showDialog(tempDate.toTypedArray(), dateFull?.courseDate!!)
                                                    tempDate.clear()
                                                }
                                            }
                                            .subscribeBy(
                                                onNext = {
                                                    dateFull = it
                                                },
                                                onComplete = {
                                                    Log.d(TAG, "GET Date Finished.")
                                                },
                                                onError = {
                                                    Log.e(TAG, it.message)
                                                }
                                            )
                                }
                            }.create().show()
                        }
                    }

                    sendTakeLeaveBtn.setOnClickListener {
                        if (choseClassTextView.text.trim() != "請選擇課程" && choseDateTextView.text.trim() != "請選擇日期" && reasonTakeLeaveEditText.text.toString().trim().isNotEmpty()) {
                            postTakeLeave(
                                TakeLeaveModel.TakeLeavePost(
                                    date,
                                    when (radioSick.isChecked || radioSomething.isChecked || radioPublic.isChecked || radioOther.isChecked) {
                                        radioSick.isChecked -> 0
                                        radioSomething.isChecked -> 1
                                        radioPublic.isChecked -> 2
                                        else -> 3
                                    },
                                    reasonTakeLeaveEditText.text.toString(),
                                    courseNameMap[choseClassTextView.text].toString(),
                                    (activity as PushPull).studentUUID!!
                                )
                            )
                        } else {
                            Toast.makeText(context, "請假格式錯誤", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                ,
                onComplete = {
                    Log.d(TAG, "Getting Data Complete!!")
                },
                onError = {
                    // TODO : Error Handling?
                    Log.e(TAG, it.message)
                }
            )
        return thisView
    }

    @SuppressLint("ShowToast")
    private fun postTakeLeave(takeleave: TakeLeaveModel.TakeLeavePost) {
        disposable = takeLeaveApiService.postTakeLeave(takeleave)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onComplete = {
                    Log.d(TAG, "Send Complete!")
                    Toast.makeText(context, "請假資料已送出", Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                },
                onError = {
                    if (it is HttpException) {
                        when (it.message!!.split(" ")[1].toInt()) {
                            601 -> {
                                Toast.makeText(context, "選擇之日期已有請假紀錄", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Log.e(TAG, it.message)
                    }
                }
            )
    }

    private fun showDialog(dateList: Array<String>, list: List<String>) {
        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context!!)

        // Set a title for alert dialog
        builder.setTitle("Choose date.")

        // Set items form alert dialog
        builder.setItems(dateList) { _, which ->

            // Get the dialog selected item
            val selected = dateList[which]

            choseDateTextView.text = selected
            choseDateBtn.setBackgroundResource(R.drawable.roll_call_select_yellow)

            date = list[which * 2]
        }
        // Display the alert dialog
        builder.create().show()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}


