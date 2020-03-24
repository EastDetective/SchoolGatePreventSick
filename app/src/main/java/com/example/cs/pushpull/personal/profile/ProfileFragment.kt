package com.example.cs.pushpull.personal.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate
import com.example.cs.pushpull.personal.PersonalApiService
import com.example.cs.pushpull.personal.model.ProfileModel
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ProfileFragment : Fragment() {

    lateinit var editBtn: Button

    lateinit var name: TextView
    lateinit var department: TextView
    lateinit var studentID: TextView
    lateinit var address: TextView
    lateinit var phoneNumber: TextView
    lateinit var admissionDate: TextView

    lateinit var portrait: ImageView
    lateinit var studentIdCard: ImageView
    lateinit var idCard: ImageView
    lateinit var idCardBackSide: ImageView
    lateinit var bankbook: ImageView

    // Api Service for Course
    private val personalApiService by lazy {
        PersonalApiService.create()
    }
    private var disposable: Disposable? = null

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    lateinit var dataFull: ProfileModel.Full

    @SuppressLint("PrivateResource")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.profile)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false).apply {

            name = findViewById(R.id.profile_name)
            studentID = findViewById(R.id.profile_student_id)
            name.text = (activity as PushPull).studentName
            studentID.text = (activity as PushPull).studentID

            department = findViewById(R.id.profile_department)
            address = findViewById(R.id.profile_address)
            phoneNumber = findViewById(R.id.profile_phone_number)
            admissionDate = findViewById(R.id.profile_admission_date)

            portrait = findViewById(R.id.profile_portrait)
            studentIdCard = findViewById(R.id.profile_studentIdCard)
            idCard = findViewById(R.id.profile_idCard)
            idCardBackSide = findViewById(R.id.profile_idCardBackSide)
            bankbook = findViewById(R.id.profile_bankbook)
            editBtn = findViewById(R.id.profile_edit_button)

            //從這裡拿
            disposable =
                personalApiService.getFullData((activity as PushPull).studentUUID!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate {
                        department.text = dataFull.studentDepartment
                        admissionDate.text = dataFull.studentRegisterDate.toDate().formatTo("yyyy.MM")
                        address.text = dataFull.studentAddress
                        phoneNumber.text = dataFull.studentPhoneNumber

                        if (dataFull.portrait.isNotEmpty()) Picasso.get().load(dataFull.portrait).fit().centerInside().into(portrait)
                        if (dataFull.studentIdCard.isNotEmpty()) Picasso.get().load(dataFull.studentIdCard).fit().centerInside().into(
                            studentIdCard
                        )
                        if (dataFull.idCard.isNotEmpty()) Picasso.get().load(dataFull.idCard).fit().centerInside().into(idCard)
                        if (dataFull.idCardBackSide.isNotEmpty()) Picasso.get().load(dataFull.idCardBackSide).fit().centerInside().into(
                            idCardBackSide
                        )
                        if (dataFull.bankbook.isNotEmpty()) Picasso.get().load(dataFull.bankbook).fit().centerInside().into(
                            bankbook
                        )

                        editBtn.setOnClickListener {
                            activity!!.supportFragmentManager.beginTransaction().apply {
                                setCustomAnimations(
                                    R.anim.abc_fade_in,
                                    R.anim.abc_fade_out,
                                    R.anim.abc_fade_in,
                                    R.anim.abc_fade_out
                                )
                                replace(R.id.push_pull_fragment_holder, EditProfileFragment().also {
                                    it.arguments = Bundle().apply {
                                        putString("profile_department", department.text.toString())
                                        putString("profile_admissionDate", dataFull.studentRegisterDate)
                                        putString("profile_address", address.text.toString())
                                        putString("profile_phoneNumber", phoneNumber.text.toString())
                                        putString("profile_portrait", dataFull.portrait)
                                        putString("profile_studentIdCard", dataFull.studentIdCard)
                                        putString("profile_idCard", dataFull.idCard)
                                        putString("profile_idCardBackSide", dataFull.idCardBackSide)
                                        putString("profile_bankbook", dataFull.bankbook)
                                        putInt("profile_type", dataFull.studentType)
                                        putString("profile_qrCode", dataFull.studentQRcode)
                                    }
                                })
                                addToBackStack(null)
                                commit()
                            }
                        }
                    }
                    .subscribeBy(
                        onNext = {
                            dataFull = it
                            (activity as PushPull).personalData = it
                        },
                        onError = {
                            Log.e("Error", it.message)
                        }
                    )
        }
    }
}
