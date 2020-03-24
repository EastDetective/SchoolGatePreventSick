package com.example.cs.pushpull.game

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.game.model.LuckyDayModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class PointExchangeFragment : Fragment() {

    private lateinit var textView: TextView
    private lateinit var pointNumber: TextView
    private lateinit var tNumber: TextView
    private lateinit var pointToticket: Button
    private var number = 0
    private var ticket = 0

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
    private lateinit var tPoint: LuckyDayModel.AllPoint

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = resources.getString(R.string.game)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_point_exchange, container, false).apply {
            textView = findViewById(R.id.textView)
            textView.paint.flags = Paint.UNDERLINE_TEXT_FLAG
            tNumber = findViewById(R.id.ticket_number)
            pointNumber = findViewById(R.id.point_number)
            getNotUsed()
            getTotal()

            val builder = AlertDialog.Builder(context)
            pointToticket = findViewById(R.id.point_to_ticket)
            pointToticket.setOnClickListener {
                builder.setTitle("確認兌換抽獎券")
                builder.setMessage("確定用100積分兌換抽獎卷一張？")
                builder.setPositiveButton("確認") { _, _ ->
                    postResponse(LuckyDayModel.PointPost(number, ticket))
                }
                builder.setNegativeButton("取消") { _, _ ->
                }
                // create dialog and show it
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun postResponse(total: LuckyDayModel.PointPost) {
        disposable =
            gameApiService.postPoint((activity as PushPull).studentUUID!!, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    number.minus(100)
                    getTotal()
                    getNotUsed()
                    val builder2 = AlertDialog.Builder(context!!)
                    builder2.setTitle("兌換成功！")
                    builder2.setMessage("獲得抽獎券一張！")
                    builder2.setPositiveButton("ok") { _, _ ->
                    }
                    val dialog = builder2.create()
                    dialog.show()
                }
                .subscribeBy(
                    onComplete = {
                        Log.d("Complete", "Post Succeed!")
                    },
                    onError = {
                        if (it is HttpException) {
                            when (it.message!!.split(" ")[1].toInt()) {
                                613 -> {
                                    Toast.makeText(context, "點數不夠兌換抽獎券", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else
                            Log.e("Error", it.message)
                    }
                )
    }

    @SuppressLint("SetTextI18n")
    private fun getNotUsed() {
        disposable =
            gameApiService.getNotUsedTicketNumber((activity as PushPull).studentUUID!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    tNumber.text = ticketNotUsed.size.toString()
                    ticket = ticketNotUsed.size
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

    @SuppressLint("SetTextI18n")
    private fun getTotal() {
        disposable =
            gameApiService.getTotalPoint((activity as PushPull).studentUUID!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    pointNumber.text = tPoint.totalPoint.toString()
                    number = tPoint.totalPoint
                }
                .subscribeBy(
                    onNext = {
                        tPoint = it
                    },
                    onError = {
                        Log.e("Error", it.message)
                    }
                )
    }
}
