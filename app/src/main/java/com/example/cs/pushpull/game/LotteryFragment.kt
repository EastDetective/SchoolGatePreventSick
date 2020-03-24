package com.example.cs.pushpull.game

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.game.model.LuckyDayModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class LotteryFragment : Fragment() {

    companion object {
        const val TAG = "LotteryExchangeFragment"
    }

    private lateinit var textView: TextView
    private lateinit var nothing: TextView
    private lateinit var lotteryList: ListView

    // Api Service for Course
    private val gameApiService by lazy {
        GameApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private var ticketNotUsed: List<LuckyDayModel.TicketNotUsed> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lottery, container, false).apply {
            textView = findViewById(R.id.textView)
            textView.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            lotteryList = findViewById(R.id.lottery_list)
            nothing = findViewById(R.id.nothing)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG,"LotteryFragment onActivityCreated")

        activity?.title = resources.getString(R.string.game)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        disposable =
            gameApiService.getNotUsedTicketNumber((activity as PushPull).studentUUID!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    val n: ArrayList<String> = arrayListOf()
                    val uuid: ArrayList<String> = arrayListOf()
                    if(ticketNotUsed.isEmpty()) {
                        nothing.visibility = View.VISIBLE
                    }
                    else{
                        for (i in 0 until ticketNotUsed.size){
                            n.add("")
                            uuid.add(ticketNotUsed[i].id)
                        }
                        lotteryList.adapter = LotteryListAdapter(this.context!!, n, uuid, activity!!)
                    }
                }
                .subscribeBy(
                    onNext = {
                        ticketNotUsed = it
                    },
                    onError = {
                        Log.e("Error", it.message)
                    }
                )
        }

    override fun onDestroyView() {
        if (view != null) {
            (view!!.parent as ViewGroup).removeView(view)
        }
        super.onDestroyView()
    }
}
