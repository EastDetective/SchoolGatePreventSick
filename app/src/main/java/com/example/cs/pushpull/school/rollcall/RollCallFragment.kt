package com.example.cs.pushpull.school.rollcall

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.now
import com.example.cs.pushpull.extension.toISO8601UTC
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.RollCallModel
import com.example.cs.pushpull.school.rollcall.scan.Scan
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class RollCallFragment : Fragment() {

    companion object {
        const val TAG = "RollCall"
    }

    // UI Nodes
    private lateinit var name: TextView
    private lateinit var idTextView: TextView
    private lateinit var textView: TextView
    private lateinit var choseBtn: Button
    private lateinit var line: View
    private lateinit var qrPart: LinearLayout
    private lateinit var qrCode: ImageView

    // Data Container
    private var inCourseList: List<RollCallModel.CourseSimple> = listOf()

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roll_call, container, false).apply {
            // Nodes Binding
            name = findViewById(R.id.roll_call_stdName_tv)
            idTextView = findViewById(R.id.roll_call_stdID_tv)
            choseBtn = findViewById(R.id.roll_call_choseBtn)
            textView = findViewById(R.id.roll_call_inside_text)
            line = findViewById(R.id.roll_call_line4)
            qrPart = findViewById(R.id.roll_call_line5)
            qrCode = findViewById(R.id.roll_call_qr)

            name.text = (activity as PushPull).studentName
            idTextView.text = (activity as PushPull).studentID
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = resources.getString(R.string.roll_call)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val studentUUID = (activity as PushPull).studentUUID!!

        name.setOnTouchListener(object : View.OnTouchListener {
            val handler = Handler()

            var numberOfTaps = 0
            var lastTapTimeMs: Long = 0
            var touchDownMs: Long = 0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> touchDownMs = System.currentTimeMillis()
                    MotionEvent.ACTION_UP -> {
                        handler.removeCallbacksAndMessages(null)

                        // Not a Tap
                        if (System.currentTimeMillis() - touchDownMs > ViewConfiguration.getTapTimeout()) {
                            numberOfTaps = 0
                            lastTapTimeMs = 0
                        }

                        // Check Single Tap or Not
                        if (numberOfTaps > 0 && System.currentTimeMillis() - lastTapTimeMs < ViewConfiguration.getDoubleTapTimeout()) {
                            numberOfTaps += 1
                        } else {
                            numberOfTaps = 1
                        }

                        lastTapTimeMs = System.currentTimeMillis()

                        // If View has been touched 5 times
                        if (numberOfTaps == 5) {
                            Log.d("Tap", "Five")
                            //activity!!.startActivity(Intent(activity, Scan::class.java))
                            startActivity(
                                Intent(activity, Scan::class.java).putExtra(
                                    "sID",
                                    (activity as PushPull).studentUUID
                                )
                            )
                        }
                    }
                }
                return true
            }
        })

        disposable = courseApiService.getAllInCourse(studentUUID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                choseBtn.setOnClickListener {
                    showDialog(inCourseList.map { it.courseName }.toTypedArray())
                }
            }
            .subscribeBy(
                onNext = {
                    inCourseList = it
                },
                onComplete = {
                    Log.d(TAG, "GET Courses Finished.")
                },
                onError = {
                    Log.e(TAG, it.message)
                }
            )
    }

    private fun showDialog(courseList: Array<String>) {

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context!!)

        // Set a title for alert dialog
        builder.setTitle("Choose a Course.")

        // Set items form alert dialog
        builder.setItems(courseList) { _, which ->

            // Get the dialog selected item
            val selected = courseList[which]

            textView.text = selected
            choseBtn.setBackgroundResource(R.drawable.roll_call_select_yellow)

            val courseId = inCourseList.find { it.courseName == selected }!!.id
            val studentId = (activity as PushPull).studentUUID!!
            var verifyString: String? = null

            Log.d(TAG, "courseID:$courseId, stdID:$studentId")

            // TODO : Error Handling
            disposable = courseApiService.putVerification(
                courseId,
                studentId,
                RollCallModel.Verification(courseId, now().toISO8601UTC(), studentId, "")
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {

                    // Check the Verify String
                    Log.d(TAG, "Verification: $verifyString")

                    // Processing QR Code
                    val bitMatrix =
                        MultiFormatWriter().encode(
                            "$studentId,$courseId,$verifyString",
                            BarcodeFormat.QR_CODE,
                            500,
                            500
                        )
                    Encoder.encode("$studentId,$courseId,$verifyString", ErrorCorrectionLevel.M)
                    val pixels = IntArray(bitMatrix.width * bitMatrix.height)
                    for (y in 0 until bitMatrix.height) {
                        for (x in 0 until bitMatrix.width) {
                            pixels[y * bitMatrix.width + x] = if (bitMatrix.get(x, y)) ContextCompat.getColor(
                                context!!,
                                R.color.black
                            ) else ContextCompat.getColor(context!!, R.color.white)
                        }
                    }
                    qrCode.setImageBitmap(
                        Bitmap.createBitmap(
                            bitMatrix.width,
                            bitMatrix.height,
                            Bitmap.Config.ARGB_8888
                        ).apply {
                            setPixels(pixels, 0, bitMatrix.width, 0, 0, bitMatrix.width, bitMatrix.height)
                        })

                    // Show INVISIBLE Nodes
                    line.visibility = View.VISIBLE
                    qrPart.visibility = View.VISIBLE
                }
                .subscribeBy(
                    onNext = {
                        // Just Need VerifyString
                        verifyString = it.verifyString
                    },
                    onComplete = {
                        Log.d(TAG, "GET VerifyString Finished.")
                    },
                    onError = {
                        Log.e(TAG, it.message)
                    }
                )
        }

        // Display the alert dialog
        builder.create().show()
    }

    // Cancel subscribe
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onDestroyView() {
        if (view != null) {
            (view!!.parent as ViewGroup).removeView(view)
        }
        super.onDestroyView()
    }
}
