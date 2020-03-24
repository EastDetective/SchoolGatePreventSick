package com.example.cs.pushpull.school.recommend

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.toISO8601UTC
import com.example.cs.pushpull.school.model.RecommendModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_recommend_choose_class.view.*
import java.sql.Timestamp
import java.util.*

class RecommendFragment : Fragment() {

    companion object {
        const val TAG = "Recommend"
    }

    private lateinit var numberPicker: NumberPicker
    private lateinit var classBtn: ImageButton
    private lateinit var classEditText: EditText

    private lateinit var classTypeTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var reasonTextView: TextView
    private lateinit var editText: EditText
    private lateinit var sendBtn: Button

    private lateinit var radioSchool0: RadioButton
    private lateinit var radioSchool1: RadioButton
    private lateinit var radioLanguage: RadioButton
    private lateinit var radioLicense: RadioButton
    private lateinit var radioOther: RadioButton

    private var courseNameArray = emptyArray<String>()
    private var courseNameList = mutableListOf<String>()

    private val recommendApiService by lazy {
        RecommendApiService.create()
    }

    private var disposable: Disposable? = null

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.recommend_course)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recommend_course, container, false).apply {

            classBtn = findViewById(R.id.recommend_btn)
            classEditText = findViewById(R.id.recommendClassEditText)

            classTypeTextView = findViewById(R.id.recommendClassTypeTextView)
            radioGroup = findViewById(R.id.recommend_radio_group)
            reasonTextView = findViewById(R.id.recommendReasonTextView)
            editText = findViewById(R.id.recommend_edit_text)
            sendBtn = findViewById(R.id.recommend_send_btn)

            radioSchool0 = findViewById(R.id.recommend_school0Radio)
            radioSchool1 = findViewById(R.id.recommend_school1Radio)
            radioLanguage = findViewById(R.id.recommend_language_radio)
            radioLicense = findViewById(R.id.recommend_license_radio)
            radioOther = findViewById(R.id.recommend_other_radio)

            classBtn.tag = 0
            classBtn.visibility = View.GONE
            classEditText.visibility = View.GONE

            getRRRecommend()
            val stamp = Timestamp((0))
            val stampNow = Timestamp(System.currentTimeMillis())
            val date = Date(stamp.time).toISO8601UTC()
            val dateNow = Date(stampNow.time).toISO8601UTC()

            Log.d(TAG, stamp.toString())
            Log.d(TAG, date)

            classBtn.setOnClickListener {
                if (classBtn.tag == 0) {
                    classTypeTextView.visibility = View.GONE
                    radioGroup.visibility = View.GONE
                    reasonTextView.visibility = View.GONE
                    editText.visibility = View.GONE

                    classEditText.isEnabled = false
                    classBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.keyboard))

                    val mDialogView =
                        LayoutInflater.from(this.context).inflate(R.layout.dialog_recommend_choose_class, null)

                    //AlertDialogBuilder
                    val mBuilder = AlertDialog.Builder(this.context)
                        .setView(mDialogView)
                    //show dialog
                    val mAlertDialog = mBuilder.show()
                    //login button click of custom layout

                    numberPicker = mDialogView.recommendNumberPicker
                    numberPicker.minValue = 0
                    numberPicker.maxValue = courseNameArray.size - 1
                    numberPicker.displayedValues = courseNameArray
                    numberPicker.wrapSelectorWheel = true

                    mDialogView.recommendOkBtn.setOnClickListener {
                        if (numberPicker.value == 0) {
                            classEditText.setText(courseNameArray[0])
                            mAlertDialog.dismiss()
                            classBtn.tag = 1
                            classEditText.setBackgroundResource(R.drawable.yellow)
                        }
                    }

                    numberPicker.setOnValueChangedListener { _, _, newVal ->
                        mDialogView.recommendOkBtn.setOnClickListener {
                            //dismiss dialog
                            mAlertDialog.dismiss()
                            //get text from EditTexts of custom layout
                            classEditText.setText(courseNameArray[newVal])

                            classBtn.tag = 1
                            classEditText.setBackgroundResource(R.drawable.yellow)

                            //set the input text in TextView
                        }
                    }

                } else {

                    classBtn.tag = 0
                    classTypeTextView.visibility = View.VISIBLE
                    radioGroup.visibility = View.VISIBLE
                    reasonTextView.visibility = View.VISIBLE
                    editText.visibility = View.VISIBLE
                    sendBtn.visibility = View.VISIBLE

                    classEditText.setBackgroundResource(R.drawable.takeleave_radio)

                    classEditText.isEnabled = true
                    classBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.recommend_arrow))
                }
            }

            if (classEditText.visibility == View.GONE) {
                sendBtn.setOnClickListener {
                    if (classEditText.text.toString().trim() != "請選擇推薦課程" && classEditText.text.toString().trim().isNotEmpty()) {
                        postRecommend(
                            RecommendModel.RecommendPost(
                                classEditText.text.toString(),
                                when (radioLanguage.isChecked || radioLicense.isChecked || radioOther.isChecked) {
                                    radioLanguage.isChecked -> 0
                                    radioLicense.isChecked -> 1
                                    else -> 2
                                },
                                if (radioSchool0.isChecked) 0 else 1,
                                editText.text.toString(),
                                "",
                                (activity as PushPull).studentUUID!!,
                                date,
                                date,
                                0,
                                "",
                                0,
                                date,
                                date,
                                dateNow
                            )
                        )
                    } else {
                        Toast.makeText(context, "推薦課程格式錯誤", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                sendBtn.setOnClickListener {
                    if (classEditText.text.toString().trim() != "請選擇推薦課程" && classEditText.text.toString().trim().isNotEmpty()
                        && editText.text.toString().trim().isNotEmpty()
                    ) {
                        postRecommend(
                            RecommendModel.RecommendPost(
                                classEditText.text.toString(),
                                when (radioLanguage.isChecked || radioLicense.isChecked || radioOther.isChecked) {
                                    radioLanguage.isChecked -> 0
                                    radioLicense.isChecked -> 1
                                    radioOther.isChecked -> 2
                                    else -> 0
                                },
                                if (radioSchool0.isChecked) 0 else 1,
                                editText.text.toString(),
                                "",
                                (activity as PushPull).studentUUID!!,
                                date,
                                date,
                                0,
                                "",
                                0,
                                date,
                                date,
                                dateNow
                            )
                        )
                    } else {
                        Toast.makeText(context, "推薦課程格式錯誤", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getRRRecommend() {

        disposable = recommendApiService.getRecommend()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterTerminate {
                classEditText.visibility = View.VISIBLE
                classBtn.visibility = View.VISIBLE
            }
            .subscribeBy(
                onNext = {
                    Log.d(TAG, it[0].courseName)
                    for (i in 0 until it.size) {
                        courseNameArray += (it[i].courseName)
                        courseNameList.add(it[i].courseName)
                    }
                    Log.d(TAG, courseNameArray.toString())
                    Log.d(TAG, courseNameList.toString())
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
    }

    @SuppressLint("ShowToast")
    private fun postRecommend(recommend: RecommendModel.RecommendPost) {
        disposable = recommendApiService.postRecommend(recommend)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
            .subscribeBy(
                onComplete = {
                    Log.d(TAG, "Send Complete!")
                    Toast.makeText(context, "Recommend Sent", Toast.LENGTH_LONG)
                    activity?.onBackPressed()
                },
                onError = {

                    Log.e(TAG, it.message)
                }
            )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}



