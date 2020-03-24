package com.example.cs.pushpull.personal.match

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs.pushpull.R

class MatchFragment : Fragment() {
    companion object {
        const val TAG = "Match"
    }

    private lateinit var viewPagerMatch: ViewPager
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            it.title = "學伴媒合"
            (it as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        return inflater.inflate(R.layout.fragment_match, container, false).apply {
            val tabArray_match: Array<String> = resources.getStringArray(R.array.match_tab)

            viewPagerMatch = findViewById(R.id.form_view_pager)
            tabLayout = findViewById(R.id.form_tabs)

            viewPagerMatch.adapter = MatchViewPagerAdapter(childFragmentManager, tabArray_match)
            tabLayout.setupWithViewPager(viewPagerMatch)
        }
    }
}