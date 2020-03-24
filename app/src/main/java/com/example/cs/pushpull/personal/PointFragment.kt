package com.example.cs.pushpull.personal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.example.cs.pushpull.PushPull

import com.example.cs.pushpull.R
import com.example.cs.pushpull.game.GameApiService
import com.example.cs.pushpull.game.model.LuckyDayModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class PointFragment : Fragment() {

    private lateinit var mypoint:TextView
    private lateinit var pointState: ImageView

    // Api Service for Course
    private val gameApiService by lazy {
        GameApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private lateinit var tPoint: LuckyDayModel.AllPoint

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.point)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_point, container, false).apply {
            mypoint = findViewById(R.id.point)
            pointState = findViewById(R.id.point_state)
            var point: Int
            disposable =
                gameApiService.getTotalPoint((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        mypoint.text = tPoint.totalPoint.toString()
                        point = tPoint.totalPoint
                        when {
                            point<50 -> pointState.setImageResource(R.drawable.point_first)
                            point in 50..59 -> pointState.setImageResource(R.drawable.point_second)
                            point in 60..99 -> pointState.setImageResource(R.drawable.point_third)
                            point > 100 -> pointState.setImageResource(R.drawable.point_fourth)
                        }
                        val pointList: ListView = findViewById(R.id.point_list)
                        val reason: ArrayList<String> = arrayListOf()
                        val get: ArrayList<Int> = arrayListOf()
                        if(tPoint.point.isNotEmpty()) {
                            for (i in 0 until tPoint.point.size){
                                if(tPoint.point[i].getPoint != 0){
                                    reason.add(tPoint.point[i].pointReason)
                                    get.add(tPoint.point[i].getPoint)
                                }
                            }
                        }
                        pointList.adapter = PointListAdapter(this.context, reason, get)
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
}
