package com.example.cs.pushpull.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class LuckyDayStartFragment : Fragment() {

    private lateinit var start: Button

    @SuppressLint("PrivateResource")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity?.title = resources.getString(R.string.game)

        // Force the item in BottomNav Selected
        (activity!!.findViewById(R.id.pushPull_navigation) as BottomNavigationView).menu.findItem(R.id.navigation_game)
            .isChecked = true

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lucky_day_start, container, false).apply {
            start = findViewById(R.id.start)
            start.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, GameFragment())
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.luckyday, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("PrivateResource")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.luckyday_record -> {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, HistoryFragment())
                    addToBackStack(null)
                    commit()
                }
                return true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }
}
