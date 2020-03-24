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
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var nothing: TextView

    // Api Service for Course
    private val gameApiService by lazy {
        GameApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private var ticketUsed: List<LuckyDayModel.TicketIsUsed> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = resources.getString(R.string.game)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false).apply {
            textView = findViewById(R.id.textView)
            textView.paint.flags = Paint.UNDERLINE_TEXT_FLAG

            nothing = findViewById(R.id.nothing)
            disposable =
                gameApiService.getHistory((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        val historyList: ListView = findViewById(R.id.history_list)
                        val date: ArrayList<String> = arrayListOf()
                        if (ticketUsed.isNotEmpty()) {
                            for (i in 0 until ticketUsed.size) {
                                date.add(ticketUsed[i].isUsedDate)
                            }
                            historyList.adapter = HistoryListAdapter(this.context, date)
                        }
                    }
                    .subscribeBy(
                        onNext = {
                            ticketUsed = it
                        },
                        onError = {
                            Log.e("Error", it.message)
                        }
                    )
        }
    }
}
