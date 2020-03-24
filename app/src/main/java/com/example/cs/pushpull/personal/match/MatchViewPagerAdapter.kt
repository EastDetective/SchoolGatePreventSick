package com.example.cs.pushpull.personal.match

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MatchViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val tabs: Array<String>
    //private val userData: ProfileModel.Full?
) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position % tabs.size) {
            1 -> MatchHelpMe()
            2 -> MatchSaveYou()
            else -> MatchRecord()
        }
    }

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = tabs[position % tabs.size]
}