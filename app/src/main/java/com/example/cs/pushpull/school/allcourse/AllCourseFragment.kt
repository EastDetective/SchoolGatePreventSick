package com.example.cs.pushpull.school.allcourse

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs.pushpull.R

class AllCourseFragment : Fragment() {

    // UI Node
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Action Bar Setting
        activity?.title = resources.getString(R.string.all_course)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_course, container, false).apply {

            // TabName (from XML)
            val tabArray: Array<String> = resources.getStringArray(R.array.all_course_tab)

            // Nodes Binding
            viewPager = findViewById(R.id.viewPager)
            tabLayout = findViewById(R.id.course_tabs)

            viewPager.offscreenPageLimit = 3
            viewPager.adapter =
                CourseViewPagerAdapter(childFragmentManager, tabArray)
            tabLayout.setupWithViewPager(viewPager)
        }
    }
}