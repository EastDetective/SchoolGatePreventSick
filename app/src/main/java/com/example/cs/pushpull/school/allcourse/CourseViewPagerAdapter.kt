package com.example.cs.pushpull.school.allcourse

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

//課程第二個進來這裡
class CourseViewPagerAdapter(fragmentManager: FragmentManager, private val tabs: Array<String>) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(p0: Int) = CourseContentFragment().apply {
        arguments = Bundle().apply {

            // Put Current Tab's Name
            putString("TabNow", getPageTitle(p0))
        }
    }

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = tabs[position % tabs.size]

}