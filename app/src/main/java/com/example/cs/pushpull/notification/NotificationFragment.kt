package com.example.cs.pushpull.notification

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.*
import com.example.cs.pushpull.R

class NotificationFragment : Fragment() {

    private lateinit var nViewPager: ViewPager
    private lateinit var nTabLayout: TabLayout
    private val nTabList: ArrayList<String> = arrayListOf("一般公告", "課程群組", "個人公告")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.notification)

        // Force the item in BottomNav Selected
        (activity?.findViewById(R.id.pushPull_navigation) as BottomNavigationView).menu.findItem(R.id.navigation_notification)
            .isChecked = true

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false).apply {

            nViewPager = findViewById(R.id.notification_viewPager)
            nTabLayout = findViewById(R.id.notification_tabs)

            nViewPager.adapter =
                NotificationViewPagerAdapter(childFragmentManager, nTabList)
            nTabLayout.setupWithViewPager(nViewPager)
        }
    }
}