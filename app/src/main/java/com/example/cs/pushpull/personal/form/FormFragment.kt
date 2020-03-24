package com.example.cs.pushpull.personal.form

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.personal.PersonalApiService
import com.example.cs.pushpull.personal.model.ProfileModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class FormFragment : Fragment() {

    companion object {
        const val TAG = "Form"
    }

    // UI Node
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    // Api Service for Course
    private val personalApiService by lazy {
        PersonalApiService.create()
    }
    private var disposable: Disposable? = null

    private var dataFull: ProfileModel.Full? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Action Bar Setting
        activity?.title = resources.getString(R.string.form)    //左上角顯示的中文字

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_form, container, false).apply {

            // TabName (from XML)
            val tabArray: Array<String> = resources.getStringArray(R.array.form_tab)

            viewPager = findViewById(R.id.form_view_pager)
            tabLayout = findViewById(R.id.form_tabs)

            disposable = personalApiService.getFullData((activity as PushPull).studentUUID!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    dataFull ?: run {
                        Log.d(TAG, "Data is Null")
                    }
                    viewPager.adapter = FormViewPagerAdapter(childFragmentManager, tabArray, dataFull)    //下一步進去哪裡 FVPA
                    tabLayout.setupWithViewPager(viewPager)
                }
                .subscribeBy(
                    onNext = {
                        dataFull = it
                    },
                    onComplete = {
                        Log.d(TAG, "Get UserData Complete!")
                    },
                    onError = {
                        Log.e("Error", it.message)
                    }
                )
        }
    }

}
