package com.example.cs.pushpull.school.previous

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs.pushpull.R

class MyCourseFragment : Fragment() {

    // UI Nodes
    private lateinit var myCViewPager: ViewPager
    private lateinit var myCTabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Add a Back Icon on ActionBar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set title on the ActionBar
        activity?.title = resources.getString(R.string.my_course)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_course, container, false).apply {

            // Tabs' Names
            val myCTabArray: Array<String> = resources.getStringArray(R.array.my_course_tab)

            // Node Binding
            myCViewPager = findViewById(R.id.my_course_viewPager)
            myCTabLayout = findViewById(R.id.my_course_tabs)

            myCViewPager.offscreenPageLimit = 2
            myCViewPager.adapter = MyCourseViewPagerAdapter(childFragmentManager, myCTabArray)
            myCTabLayout.setupWithViewPager(myCViewPager)

        }
    }
}