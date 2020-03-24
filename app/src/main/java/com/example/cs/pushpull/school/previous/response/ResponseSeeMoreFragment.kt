package com.example.cs.pushpull.school.previous.response

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.cs.pushpull.R

class ResponseSeeMoreFragment : Fragment() {
    private lateinit var seeMoreResponseId: TextView
    private lateinit var seeMoreResponseDate: TextView
    private lateinit var seeMoreResponseText: TextView
    private lateinit var seeMoreResponseTeacher: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "課程學習反應"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response_see_more, container, false).apply {

            seeMoreResponseId = findViewById(R.id.seeMoreResponseId)
            seeMoreResponseDate = findViewById(R.id.seeMoreResponseDate)
            seeMoreResponseText = findViewById(R.id.seeMoreResponseText)
            seeMoreResponseTeacher = findViewById(R.id.seeMoreResponseTeacher)

            seeMoreResponseId.text = arguments!!.getString("studentID")
            seeMoreResponseText.text = arguments!!.getString("studentContent")
            seeMoreResponseDate.text = arguments!!.getString("evaluationTime")
            seeMoreResponseTeacher.text=arguments!!.getString("teacherContent")
            if(seeMoreResponseTeacher.text.length==0){
                seeMoreResponseTeacher.text="（（ 中心目前尚未回覆 ））"
            }
        }
    }
}



