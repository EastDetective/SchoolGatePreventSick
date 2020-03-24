package com.example.cs.pushpull.game

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import com.example.cs.pushpull.R

@Suppress("NAME_SHADOWING")
class LotteryListAdapter(context: Context, nList: ArrayList<String>, idList: ArrayList<String>, val activity: FragmentActivity) : BaseAdapter() {

    private var n: ArrayList<String>? = null
    private var inflater: LayoutInflater? = null
    private lateinit var exchange: Button
    private var lotteryId: ArrayList<String>? = null

    init {
        n = nList
        inflater = LayoutInflater.from(context)
        lotteryId = idList
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView: View = inflater!!.inflate(R.layout.item_lottery, parent, false)
        exchange = convertView.findViewById(R.id.button)
        exchange.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.push_pull_fragment_holder, LotteryExchangeFragment().also {
                    it.arguments = Bundle().apply {
                        putString("lotteryUUID", lotteryId!![position])
                    }})
                .addToBackStack(null)
                .commit()
        }
        return convertView
    }

    override fun getItem(position: Int): Any {
        return lotteryId!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return lotteryId!!.size
    }

}