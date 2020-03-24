package com.example.cs.pushpull.notification

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class NotificationViewPagerAdapter(fragmentManager: FragmentManager, private val tabs: ArrayList<String>) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(p0: Int): NotificationContentFragment {
        return NotificationContentFragment().apply {
            arguments = Bundle().apply { putString("nTabNow", getPageTitle(p0)) }
        }
    }

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = tabs[position % tabs.size]

}