package com.example.cs.pushpull.school.previous.response

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.widget.Button
import com.example.cs.pushpull.R

class ResponseFragment : Fragment() {

    // UI Nodes
    private lateinit var rViewPager: ViewPager
    private lateinit var rTabLayout: TabLayout
    private lateinit var newResponseBtn: Button

    // TabName
    private val rTabList: ArrayList<String> = arrayListOf("公開", "私人")

    @SuppressLint("PrivateResource")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Action Bar Setting
        activity?.title = resources.getString(R.string.response)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response, container, false).apply {

            // Nodes Binding
            rViewPager = findViewById(R.id.response_viewPager)
            rTabLayout = findViewById(R.id.response_tabs)
            newResponseBtn = findViewById(R.id.response_newBtn)

            // Get argument from last Fragment
            val courseId = arguments?.getString("courseId")!!

            // Setup Viewpager
            rViewPager.offscreenPageLimit = 1
            rViewPager.adapter = ResponseViewPagerAdapter(
                childFragmentManager,
                rTabList,
                courseId
            )
            rTabLayout.setupWithViewPager(rViewPager)

            // Button OnClick Function
            newResponseBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, ResponseNewFragment().apply {
                        arguments = Bundle().apply {

                            putString("courseId", courseId)
                        }
                    })
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }
}


