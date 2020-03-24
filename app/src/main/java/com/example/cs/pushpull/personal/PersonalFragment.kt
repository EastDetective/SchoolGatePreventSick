package com.example.cs.pushpull.personal

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.game.GameApiService
import com.example.cs.pushpull.game.model.LuckyDayModel
import com.example.cs.pushpull.personal.form.FormFragment
import com.example.cs.pushpull.personal.match.MatchFragment
import com.example.cs.pushpull.personal.profile.ProfileFragment
import com.example.cs.pushpull.personal.timetable.TimetableFragment
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@Suppress("DEPRECATION")
class PersonalFragment : Fragment() {

    private lateinit var profileBtn: RelativeLayout
    private lateinit var timetableBtn: RelativeLayout
    private lateinit var formBtn: RelativeLayout
    private lateinit var matchBtn: RelativeLayout
    private lateinit var pointBtn: Button
    private lateinit var pointShow: TextView
    private lateinit var version: TextView

    lateinit var name: TextView
    lateinit var studentID: TextView

    private lateinit var portrait: ImageView

    // Api Service for Course
    private val gameApiService by lazy {
        GameApiService.create()
    }

    private lateinit var tPoint: LuckyDayModel.AllPoint

    @SuppressLint("PrivateResource", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.personal)

        // Hide Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Force the item in BottomNav Selected
        (activity?.findViewById(R.id.pushPull_navigation) as BottomNavigationView).menu.findItem(R.id.navigation_personal)
            .isChecked = true

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal, container, false).apply {

            version = findViewById(R.id.version)
            version.text =
                "Build." + context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString()
            version.setTextColor(Color.argb(40, 255, 255, 255))

            pointShow = findViewById(R.id.point_show)
            disposable =
                gameApiService.getTotalPoint((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        pointShow.text = tPoint.totalPoint.toString()
                    }
                    .subscribeBy(
                        onNext = {
                            tPoint = it
                        },
                        onError = {
                            Log.e("Error", it.message)
                        }
                    )

            name = findViewById(R.id.personal_name)
            studentID = findViewById(R.id.personal_studentID)

            name.text = (activity as PushPull).studentName
            studentID.text = (activity as PushPull).studentID

            // Match ID to Buttons
            profileBtn = findViewById(R.id.personal_profile)
            timetableBtn = findViewById(R.id.personal_schoolTimetables)
            formBtn = findViewById(R.id.personal_online_form)
            matchBtn = findViewById(R.id.personal_match)
            pointBtn = findViewById(R.id.personal_point)

            // Listener of Buttons
            profileBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, ProfileFragment())
                    addToBackStack(null)
                    commit()
                }
            }
            timetableBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder,
                        TimetableFragment()
                    )
                    addToBackStack(null)
                    commit()
                }
            }
            formBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder, FormFragment())
                    addToBackStack(null)
                    commit()
                }
            }
            matchBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction().apply {
                    setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    replace(R.id.push_pull_fragment_holder,
                        MatchFragment()
                    )
                    addToBackStack(null)
                    commit()
                }
            }
            pointBtn.setOnClickListener {
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

            portrait = findViewById(R.id.personal_image)
            disposable =
                personalApiService.getFullData((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onNext = {
                            if (it.portrait.isNotEmpty()) Picasso.get().load(it.portrait).fit().centerInside().into(portrait)
                        },
                        onError = {
                            Log.e("Error", it.message)
                        }
                    )
        }
    }

    // Api Service for Course
    private val personalApiService by lazy {
        PersonalApiService.create()
    }

    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}