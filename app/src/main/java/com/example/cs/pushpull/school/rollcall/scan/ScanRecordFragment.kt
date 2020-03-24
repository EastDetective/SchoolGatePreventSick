package com.example.cs.pushpull.school.rollcall.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs.pushpull.R
import com.example.cs.pushpull.school.CourseApiService
import io.reactivex.disposables.Disposable

class ScanRecordFragment : Fragment(){

    companion object {
        const val TAG = "ScanRecordFragment"
    }

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_record, container, false).apply {
            // Node Binding

        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
