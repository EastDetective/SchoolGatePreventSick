package com.example.cs.pushpull.school.previous.response

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ResponseViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val tabs: ArrayList<String>,
    private val courseId: String
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(p0: Int): ResponseContentFragment {
        return ResponseContentFragment().apply {
            arguments = Bundle().apply {

                // Put Current Tab's Name
                putString("TabNow", getPageTitle(p0))
                putString("courseId", courseId)
            }
        }
    }

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = tabs[position % tabs.size]

}