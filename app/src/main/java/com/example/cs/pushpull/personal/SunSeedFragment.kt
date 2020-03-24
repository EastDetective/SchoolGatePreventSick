package com.example.cs.pushpull.personal

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.cs.pushpull.R

class SunSeedFragment : Fragment() {

    private lateinit var webBtn:Button

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.abroad)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sun_seed, container, false).apply {
            webBtn = findViewById(R.id.goSunSeedWedButton)
            webBtn.setOnClickListener{
                var intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://ctld.utaipei.edu.tw/sun/view?feed=001&_b=%5B%7B%22t%22%3A%22%5Cu967d%5Cu5149%5Cu7a2e%5Cu5b50%5Cu734e%5Cu88dc%5Cu52a9%5Cu5c08%5Cu5340+Subsidy+for+Studying+Guidance%22%2C%22lt%22%3A%22%5Cu967d%5Cu5149%5Cu7a2e%5Cu5b50%5Cu734e%5Cu88dc%5Cu52a9%5Cu5c08%5Cu5340+Subsidy+for+Studying+Guidance%22%2C%22p%22%3A%22index%22%2C%22a%22%3A%22%22%7D%5D")
                startActivity(intent)

            }
        }
    }

    fun onClick_SunSeed(view : View){

    }

}
