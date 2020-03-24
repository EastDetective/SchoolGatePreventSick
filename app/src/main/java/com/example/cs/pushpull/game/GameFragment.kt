package com.example.cs.pushpull.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.*
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.game.model.LuckyDayModel
import com.example.cs.pushpull.personal.PointFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class GameFragment : Fragment() {

    private lateinit var luckyPersonalpoint: Button
    private lateinit var luckyTicket: Button
    private lateinit var luckyPoint: Button
    private lateinit var myTotalPoint: TextView
    private lateinit var luckyGo: ImageView
    private lateinit var luckyTurntable: ImageView
    private lateinit var luckyText: ImageView
    private var finalResultOnLuckyBoard = 1

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
    private lateinit var tResult : LuckyDayModel.LuckyResult

    @SuppressLint("PrivateResource")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tResult = LuckyDayModel.LuckyResult(1,1,1)
        setHasOptionsMenu(true)
        activity?.title = resources.getString(R.string.game)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false).apply {

            // Match ID
            luckyPersonalpoint = findViewById(R.id.lucky_personalpoint)
            luckyTicket = findViewById(R.id.lucky_ticket)
            luckyPoint = findViewById(R.id.lucky_exchange)
            luckyGo = findViewById(R.id.lucky_go)
            luckyTurntable = findViewById(R.id.lucky_turntable)
            luckyText = findViewById(R.id.lucky)

            //設置LUCKY! 字樣為不可見
            luckyText.showOrInvisible(false)

            // Listener
            luckyPersonalpoint.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, PointFragment())
                    addToBackStack(null)
                    commit()
                }
            }

            luckyTicket.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, LotteryFragment())
                    addToBackStack(null)
                    commit()
                }
            }

            var rotateHowManyRound: Int
            val random = Random()
            var rotateSection = 0

            luckyGo.setOnClickListener {
                disposable =
                    gameApiService.getLuckyDayResult((activity as PushPull).studentUUID!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate {
                            finalResultOnLuckyBoard = tResult.index
                        }
                        .subscribeBy(
                            onNext = {
                                tResult = it
                                finalResultOnLuckyBoard = tResult.index
                                when (finalResultOnLuckyBoard) {                      //設定結果需多轉幾個90度
                                    0 -> rotateSection = 0                          //索引0是恭喜中獎 不需多轉
                                    1 -> rotateSection = 2                          //索引1是再接再厲 多轉180
                                    2 -> rotateSection = 3                          //索引2是+10 多轉270
                                    3 -> rotateSection = 1                          //索引3是+30 多轉90
                                    else -> {
                                        Log.e("Error", "LuckyDay return index is wrong")
                                    }

                                }

                                rotateHowManyRound = random.nextInt(8 - 4 + 1) + 4    //設定轉的圈數4~8圈
                                val rotatedAngleInSection = (random.nextInt(85 - 5 + 1) + 5) //設定停在格子中的度數會是5度至85度
                                val totalRotate = rotateHowManyRound*360 + rotateSection*90 + rotatedAngleInSection + 50
                                val rotateAnimation = RotateAnimation(
                                    0f, totalRotate * 1f,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5F,
                                    RotateAnimation.RELATIVE_TO_SELF, 0.5F
                                )
                                rotateAnimation.duration = 3000
                                rotateAnimation.interpolator
                                rotateAnimation.fillAfter = true
                                luckyTurntable.startAnimation(rotateAnimation)

                                var resultMsg = "Lucky day結果存放在此"
                                var increasePoint = 0
                                var luckyShow = true
                                when (finalResultOnLuckyBoard) {
                                    0 -> {
                                        resultMsg = "Lucky !!!  +100 積分!!!"
                                        increasePoint = 0
                                    }
                                    1 -> {
                                        resultMsg = "再接再厲~"
                                        increasePoint = 0
                                        luckyShow = false
                                    }
                                    2 -> {
                                        resultMsg = "Lucky !  +10 積分"
                                        increasePoint = 10
                                    }
                                    3 -> {
                                        resultMsg =   "Lucky !!  +30 積分"
                                        increasePoint = 30
                                    }
                                }
                                Handler().postDelayed({
                                    Toast.makeText(context,  resultMsg, Toast.LENGTH_LONG).show()
                                    if(luckyShow) luckyText.showOrInvisible(true)
                                    updateTotalPoint()
                                    myTotalPoint.text = (tPoint.totalPoint + increasePoint).toString()
                                },3000)
                                //留著學習錯誤的程式碼（以下程式碼確定錯誤 但有學習錯誤經驗價值 所以才會留著）
//                                Timer().schedule(1000, 5000){
//                                    Toast.makeText(context, "Lucky !!!  恭喜中獎 !!!", Toast.LENGTH_LONG).show()    //Timer裡面放這行會爆
//                                    Log.e("cafˊe", "Lucky !!!  恭喜中獎 !!!")                                  //Timer裡面放這行會過
//                                }
                                Log.d("轉盤已轉動", "結果是 : $resultMsg")
                                disposable =
                                    gameApiService.postIncreaseePoint()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeBy(
                                            onComplete = {
                                                Log.d("LuckyGooo", "已轉動完畢")
                                            },
                                            onError = {
                                                Log.e("Error", it.message)
                                            }
                                        )
                            },
                            onError = {
                                if (it is HttpException) {
                                    when (it.message!!.split(" ")[1].toInt()) {
                                        612 -> {
                                            Toast.makeText(context, "你今天轉過了喔", Toast.LENGTH_LONG).show()
                                            Log.e("Error", "轉過了 錯誤代碼 ： 612")
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(context, "伺服器維修中", Toast.LENGTH_LONG).show()
                                    Log.e("Error", it.message)
                                }
                            }
                        )
            }



            myTotalPoint = findViewById(R.id.total_point)
            disposable =
                gameApiService.getTotalPoint((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        myTotalPoint.text = tPoint.totalPoint.toString()
                        luckyPoint.setOnClickListener {
                            activity!!.supportFragmentManager.beginTransaction().apply {
                                setCustomAnimations(
                                    R.anim.abc_fade_in,
                                    R.anim.abc_fade_out,
                                    R.anim.abc_fade_in,
                                    R.anim.abc_fade_out
                                )
                                replace(R.id.push_pull_fragment_holder, PointExchangeFragment())
                                addToBackStack(null)
                                commit()
                            }
                        }
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.luckyday, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }
    @SuppressLint("PrivateResource")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
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

    private fun View.showOrInvisible(show: Boolean) {
        visibility = if(show) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    @SuppressLint("PrivateResource")
    private fun updateTotalPoint(){
        Log.d("456", "現在是"+tPoint.totalPoint)
        disposable =
            gameApiService.getTotalPoint((activity as PushPull).studentUUID!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    luckyPoint.setOnClickListener {
                        activity!!.supportFragmentManager.beginTransaction().apply {
                            setCustomAnimations(
                                R.anim.abc_fade_in,
                                R.anim.abc_fade_out,
                                R.anim.abc_fade_in,
                                R.anim.abc_fade_out
                            )
                            replace(R.id.push_pull_fragment_holder, PointExchangeFragment())
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
                .subscribeBy(
                    onNext = {
                        tPoint = it
                    },
                    onError = {
                        Log.e("Error", it.message)
                    }
                )
        Log.d("456", "轉後是"+tPoint.totalPoint)
        myTotalPoint.text = tPoint.totalPoint.toString()
    }
}