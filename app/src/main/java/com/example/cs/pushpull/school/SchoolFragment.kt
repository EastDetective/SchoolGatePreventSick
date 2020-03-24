package com.example.cs.pushpull.school

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.school.allcourse.AllCourseFragment
import com.example.cs.pushpull.school.favorite.FavoriteCourseFragment
import com.example.cs.pushpull.school.leave.TakeLeaveFragment
import com.example.cs.pushpull.school.model.CourseModel
import com.example.cs.pushpull.school.previous.MyCourseFragment
import com.example.cs.pushpull.school.recommend.RecommendFragment
import com.example.cs.pushpull.school.rollcall.RollCallFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.loader.ImageLoader
import io.reactivex.disposables.Disposable

class SchoolFragment : Fragment() {

    companion object {
        const val TAG = "School"
    }

    // UI Nodes
    private lateinit var allCourseBtn: CardView
    private lateinit var favoriteCourseBtn: CardView
    private lateinit var recommendCourseBtn: CardView
    private lateinit var myCourseBtn: CardView
    private lateinit var takeLeaveBtn: CardView
    private lateinit var rollCallBtn: CardView
    private lateinit var banner: Banner

    // Api Service for Course
    private val courseApiService by lazy {
        CourseApiService.create()
    }
    private var disposable: Disposable? = null

    // Data Container
    private var bannerContent: List<CourseModel.Banner>? = null

    @SuppressLint("PrivateResource")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.title = resources.getString(R.string.course)

        // Force the item in BottomNav Selected
        (activity?.findViewById(R.id.pushPull_navigation) as BottomNavigationView).menu.findItem(R.id.navigation_school)
            .isChecked = true

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_school, container, false).apply {

            Log.d(TAG, "UUID:${(activity as PushPull).studentUUID}")

            banner = findViewById(R.id.school_banner)

            val images: MutableList<Int> = mutableListOf(
                R.drawable.test_banner1, R.drawable.test_banner2, R.drawable.test_banner3
            )
            val titles: MutableList<String> = mutableListOf(
                "", "", ""
            )
            val webPage = arrayOf("", "", "http://ctld.utaipei.edu.tw/home/") // TODO Delete

            bannerContent?.onEach {
                //                images.add(it.picture)
//                titles.add(it.title)
            }

            banner.setImageLoader(object : ImageLoader() {
                // Using Picasso to Load Images
                override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) =
                    Picasso.get().load(path as Int).fit().centerInside().into(imageView)
            })
            banner.setImages(images)
                .setOnBannerListener {
                    //                    if (bannerContent!![it].webPage != "") {
//                        startActivity(
//                            Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse(bannerContent!![it].webPage)
//
//                            )
//                        )
//                    }
                    if (webPage[it] != "") startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(webPage[it])
                        )
                    )
                }
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
            banner.setBannerTitles(titles)
            banner.start()


            // TODO Banner
//            disposable = courseApiService.getBannerContent()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doAfterTerminate {
//
//                    // Banner Setup
//                    val images: MutableList<String> = mutableListOf()
//                    val titles: MutableList<String> = mutableListOf()
//
//                    bannerContent?.onEach {
//                        images.add(it.picture)
//                        titles.add(it.title)
//                    }
//
//                    banner.setImageLoader(object : ImageLoader() {
//                        // Using Picasso to Load Images
//                        override fun displayImage(context: Context?, path: Any?, imageView: ImageView?) =
//                            Picasso.get().load(path.toString()).fit().centerInside().into(imageView)
//                    })
//                    banner.setImages(images)
//                        .setOnBannerListener {
//                            if(bannerContent!![it].webPage != ""){
//                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(bannerContent!![it].webPage)))
//                            }
//                        }
//                    banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
//                    banner.setBannerTitles(titles)
//                    banner.start()
//
//                }
//                .subscribeBy(
//                    onNext = {
//                        bannerContent = it
//                    },
//                    onComplete = {
//                        Log.d(TAG, "Get Banner Content Succeed!")
//                    },
//                    onError = {
//                        Log.e(TAG, it.message)
//                    }
//                )

            // Buttons to each Fragment
            allCourseBtn = findViewById(R.id.school_all_course)
            allCourseBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    .replace(R.id.push_pull_fragment_holder, AllCourseFragment())
                    .addToBackStack(null)
                    .commit()
            }
            favoriteCourseBtn = findViewById(R.id.school_favorite_course)
            favoriteCourseBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    .replace(R.id.push_pull_fragment_holder, FavoriteCourseFragment())
                    .addToBackStack(null)
                    .commit()
            }
            recommendCourseBtn = findViewById(R.id.school_recommend_course)
            recommendCourseBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    .replace(R.id.push_pull_fragment_holder, RecommendFragment())
                    .addToBackStack(null)
                    .commit()
            }
            myCourseBtn = findViewById(R.id.school_my_course)
            myCourseBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    .replace(R.id.push_pull_fragment_holder, MyCourseFragment())
                    .addToBackStack(null)
                    .commit()
            }
            takeLeaveBtn = findViewById(R.id.school_take_leave)
            takeLeaveBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    .replace(R.id.push_pull_fragment_holder, TakeLeaveFragment())
                    .addToBackStack(null)
                    .commit()
            }
            rollCallBtn = findViewById(R.id.school_roll_call)
            rollCallBtn.setOnClickListener {
                activity!!.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out,
                        R.anim.abc_fade_in,
                        R.anim.abc_fade_out
                    )
                    .replace(R.id.push_pull_fragment_holder, RollCallFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onPause() {
        disposable?.dispose()
        super.onPause()
    }
}