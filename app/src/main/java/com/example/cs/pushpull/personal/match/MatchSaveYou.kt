package com.example.cs.pushpull.personal.match

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.R
import kotlinx.android.synthetic.main.fragment_match_test.view.*

class MatchSaveYou : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            it.title = "學伴媒合"
            (it as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        return inflater.inflate(R.layout.fragment_match_test, container, false).apply {
            var textView : TextView = this.findViewById(R.id.textView9)
            textView.setText("textView2")
        }
    }
}