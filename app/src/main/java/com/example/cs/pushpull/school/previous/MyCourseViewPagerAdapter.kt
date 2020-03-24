package com.example.cs.pushpull.school.previous

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyCourseViewPagerAdapter(fragmentManager: FragmentManager, private val tabs: Array<String>) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(p0: Int): MyCourseContentFragment {
        return MyCourseContentFragment().apply {
            arguments = Bundle().apply { putString("myCTabNow", getPageTitle(p0)) }
        }
    }

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = tabs[position % tabs.size]

}