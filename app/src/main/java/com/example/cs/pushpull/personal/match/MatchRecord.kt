package com.example.cs.pushpull.personal.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs.pushpull.R
import java.util.ArrayList

class MatchRecord : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val demoStr = ArrayList<String>()
        for (i in 0..12) {
            demoStr.add("第"+i+"項")
        }

        activity?.let {
            it.title = "學伴媒合"
            (it as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        return inflater.inflate(R.layout.fragment_match_record, container, false).apply {

            var llm = LinearLayoutManager(this.context)
            llm.orientation = LinearLayoutManager.VERTICAL
            var recyclerView : RecyclerView = findViewById(R.id.recycleView_match_record)
            recyclerView.layoutManager = llm
            recyclerView.adapter = MatchRecyclerViewAdapter(demoStr)



        }
    }
}