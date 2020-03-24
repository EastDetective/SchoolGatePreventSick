package com.example.cs.pushpull.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cs.pushpull.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers



class LotteryExchangeFragment : Fragment() {

    companion object {
        const val TAG = "LotteryExchangeFragment"
    }

    private lateinit var luckyExchange: Button
    var builder: AlertDialog.Builder? = null

    // Api Service for Course
    private val gameApiService by lazy {
        GameApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lottery_exchange, container, false).apply {
            luckyExchange = findViewById(R.id.lucky_exchange)
            builder = AlertDialog.Builder(context)
        }
    }

    @SuppressLint("MissingSuperCall", "PrivateResource")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = resources.getString(R.string.game)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d(TAG,"onActivityCreated")
        luckyExchange.setOnClickListener {
            // setup dialog builder

            builder?.setTitle("確認兌換抽獎券")
            builder?.setMessage("兌換鈕需由教發中心人員點選。請確認您已在教發中心，並出示抽獎券畫面。")
            builder?.setPositiveButton("確認") { _, _ ->
                Log.d("兌換的抽獎券號碼", arguments?.getString("lotteryUUID")!!)
                disposable =
                    gameApiService.putLotteryChanged(arguments?.getString("lotteryUUID")!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy(
                            onComplete = {
                                Log.d(TAG, "put lotteryUUID finished")
                                activity!!.supportFragmentManager.beginTransaction().apply {
                                    setCustomAnimations(
                                        R.anim.abc_fade_in,
                                        R.anim.abc_fade_out,
                                        R.anim.abc_fade_in,
                                        R.anim.abc_fade_out
                                    )

                                    Log.d(TAG,"replace")
                                    replace(R.id.push_pull_fragment_holder, LotteryFragment())
                                    addToBackStack(null)
                                    commit()
                                }
                            },
                            onError = {
                                Log.e(TAG, it.message)
                            }
                        )

            }
            builder?.setNegativeButton("取消") { _, _ ->
                println("cancel")
            }
            // create dialog and show it
            val dialog = builder?.create()
            dialog?.show()
        }
    }

    override fun onDestroyView() {
       if (view != null) {
            (view!!.parent as ViewGroup).removeView(view)
        }
        super.onDestroyView()
    }
}
