package com.example.cs.pushpull.school.previous.response

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate
import com.example.cs.pushpull.school.CourseApiService
import com.example.cs.pushpull.school.model.ResponseModel
import io.reactivex.disposables.Disposable

class ResponseListAdapter(context: Context, responseList: List<ResponseModel.Response>) : BaseAdapter() {

    // Display Data (Processed)
    private var response: List<ResponseModel.Response>? = null
    private var inflater: LayoutInflater? = null
    private lateinit var courseApiService: CourseApiService
    private var disposable: Disposable? = null
    private var courseID: String? = null
    private var supFragmentManager: FragmentManager? = null

    constructor(
        context: Context,
        ResponseList: List<ResponseModel.Response>,
        courseApiService: CourseApiService,
        disposable: Disposable?,
        courseID: String, supFragmentManager: FragmentManager
    ) : this(context, ResponseList) {
        this.courseID = courseID
        this.courseApiService = courseApiService
        this.disposable = disposable
        this.supFragmentManager = supFragmentManager
    }

    init {
        response = responseList
        inflater = LayoutInflater.from(context)
    }

    @SuppressLint("ViewHolder", "NewApi", "PrivateResource")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View =
        inflater!!.inflate(R.layout.item_course_response, parent, false).apply {

            // Binding Nodes and filling text in
            val responseIDTextView: TextView = findViewById(R.id.responseRow_idText)
            responseIDTextView.text = response!![position].studentID

            val responseTimeTextView: TextView = findViewById(R.id.responseRow_dateText)
            responseTimeTextView.text = response!![position].evaluationTime.toDate()
                .formatTo("yyyy/MM/dd")

            val responseEditTextView: TextView = findViewById(R.id.responseRow_editText)
            responseEditTextView.text = response!![position].studentContent

            val moreResponseBtn: Button = findViewById(R.id.responseRow_btn)
            moreResponseBtn.setOnClickListener {
                supFragmentManager!!.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, ResponseSeeMoreFragment().apply {
                        arguments = Bundle().apply {

                            putString("studentID", response!![position].studentID)
                            putString(
                                "evaluationTime", response!![position].evaluationTime.toDate()
                                    .formatTo("yyyy/MM/dd")
                            )
                            putString("studentContent", response!![position].studentContent)
                            putString("teacherContent", response!![position].teacherResponse)
                        }
                    })
                    addToBackStack(null)
                    commit()
                }
            }

            requestLayout()
        }

    override fun getItem(position: Int) = response!![position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = response!!.size
}