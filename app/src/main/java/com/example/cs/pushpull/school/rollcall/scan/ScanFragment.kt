package com.example.cs.pushpull.school.rollcall.scan

import android.content.DialogInterface
import android.graphics.PointF
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.now
import com.example.cs.pushpull.extension.toISO8601UTC
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.RollCallModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.FileNotFoundException

class ScanFragment : Fragment(), QRCodeReaderView.OnQRCodeReadListener {

    companion object {
        const val TAG = "ScanFragment"
    }

    // UI Nodes
    private lateinit var qrCodeReaderView: QRCodeReaderView
    private lateinit var backBtn: Button
    private lateinit var record: Button

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, arguments?.getString("rcaccountID")?: "No RCAccount")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false).apply {
            // Node Binding
            qrCodeReaderView = findViewById(R.id.scan_qr_reader)
            backBtn = findViewById(R.id.scan_back_btn)

            // Setting about Reader
            qrCodeReaderView.let {
                it.setOnQRCodeReadListener(this@ScanFragment)
                it.setQRDecodingEnabled(true)
                it.setAutofocusInterval(5000L)
                it.setBackCamera()
            }

            // Button Setting
            backBtn.setOnClickListener {
                activity!!.supportFragmentManager.popBackStack()
            }

            //TODO--
            // 點名名單一覽介面(待做)
//            record = findViewById(R.id.scan_record)
//            record.setOnClickListener {
//                activity!!.supportFragmentManager.beginTransaction()
//                    .replace(R.id.scan_fragment_holder, ScanRecordFragment())
//                    .addToBackStack(null)
//                    .commit()
//            }
        }
    }

    override fun onQRCodeRead(text: String?, points: Array<PointF>?) {
        qrCodeReaderView.setQRDecodingEnabled(false)
        qrCodeReaderView.stopCamera()
        showDialog(text!!)
    }

    override fun onResume() {
        super.onResume()
        qrCodeReaderView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qrCodeReaderView.stopCamera()
        disposable?.dispose()
    }

    private fun showDialog(text: String) {
        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(context!!)

        // Set a title for alert dialog
        builder.setTitle("Sure to do this Roll Call.")

        // Set a message for alert dialog
        builder.setMessage("Are you sure?")

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    if (text.split(",").size == 3) {
                        disposable = courseApiService.addARollCall(
                            RollCallModel.AddRollCall(
                                now().toISO8601UTC(),
                                text.split(",")[1],
                                text.split(",")[0],
                                text.split(",")[2],
                                arguments?.getString("rcaccountID")!!,
                                arguments?.getString("courseCode")!!
                            )
                        )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy(
                                onComplete = {
                                    Toast.makeText(activity, "點名成功", Toast.LENGTH_LONG).show()
                                },
                                onError = {
                                    if (it is HttpException) {
                                        when (it.code()) {
                                            600 -> {
                                                Toast.makeText(
                                                    activity,
                                                    "此學生未上這門課",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                            605, 609 -> {
                                                Toast.makeText(
                                                    activity,
                                                    "輸入的課程代碼與掃描到的課程不符",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            601 -> {
                                                Toast.makeText(
                                                    activity,
                                                    "今天已點過名",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                            else -> {
                                                Log.e(TAG, it.message())
                                            }
                                        }
                                    } else {
                                        Log.e(TAG, it.message)
                                        Toast.makeText(context, "未知問題，請重試", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            )
                    } else {
                        Toast.makeText(context, "此QRcode格式不符", Toast.LENGTH_LONG).show()
                    }
                    qrCodeReaderView.setQRDecodingEnabled(true)
                    qrCodeReaderView.startCamera()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
//                    toast("Negative/No button clicked.")
                    qrCodeReaderView.setQRDecodingEnabled(true)
                    qrCodeReaderView.startCamera()
                }
//                DialogInterface.BUTTON_NEUTRAL -> toast("Neutral/Cancel button clicked.")
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)

        // Set the alert dialog neutral/cancel button
//        builder.setNeutralButton("CANCEL", dialogClickListener)

        // Display the alert dialog
        builder.create().show()
    }
}
