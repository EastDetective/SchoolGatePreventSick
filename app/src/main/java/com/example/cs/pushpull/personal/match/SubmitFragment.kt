package com.example.cs.pushpull.personal.match

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.cs.pushpull.R


class SubmitFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_match_submit,container,false).apply {
            showDialog()
        }
    }

    private fun showDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("申請成功")
        builder.setMessage("請點擊確認")
        builder.setPositiveButton("確認"){
                dialog, which -> dialog.cancel()
        }
        builder.show()
    }

}