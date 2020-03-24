package com.example.cs.pushpull.personal.form

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.getOpenglRenderLimitValue
import com.example.cs.pushpull.personal.model.FormModel
import com.example.cs.pushpull.personal.profile.EditContentFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_choose_class.view.*
import kotlinx.android.synthetic.main.fragment_form_content_apply.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import java.io.ByteArrayOutputStream as ByteArrayOutputStream1

class FormContentFragment : Fragment() {

    companion object {
        const val TAG = "FormContent"
    }

    // UI Node
    private lateinit var gradeOrLicenseUpload: Button
    private lateinit var sureEditBtn: Button
    private lateinit var hereIsWhereUploadedImage: ImageView
    private lateinit var succeedShow: RelativeLayout
    private lateinit var failShow: RelativeLayout

    private lateinit var gradeBetterTextView: TextView
    private lateinit var gradeBetterExplainEditText: EditText
    private lateinit var gradeTextView: TextView
    private lateinit var schoolYearTextView: TextView
    private lateinit var choseSchoolYearTextView: TextView
    private lateinit var choseSchoolYearBtn: Button
    private lateinit var licenseColumn: TableLayout
    private lateinit var licenseName: EditText
    private lateinit var licenseDate: EditText
    private lateinit var radioGroup1: RadioGroup
    private lateinit var radioGroup2: RadioGroup
    private lateinit var radioCountry: RadioButton
    private lateinit var radioGovernment: RadioButton
    private lateinit var radioNational: RadioButton
    private lateinit var radioForeign: RadioButton
    private lateinit var radioElse: RadioButton
    private lateinit var profileFrameLayout: FrameLayout

    private lateinit var radioGrade: RadioButton
    private lateinit var radioLicense: RadioButton
    var isChanged = arrayOf(false)

    private var gradeApplyAllow: Int = -1
    private var licenseApplyAllow: Int = -1

    // Raw Data Container
    private var myApply: List<FormModel.Apply> = listOf()
    private var myLicense: List<FormModel.LicenseGrade> = listOf()
    private lateinit var numberPickers: NumberPicker
    private var schoolYearArray = arrayOf("104", "105", "106", "107")
    private var semesterArray = arrayOf("上學期", "下學期")
    private var schoolYearPick: Int = 0                //學生所選的學年索引值
    private var semesterPick: Int = 0                  //學生所選的學期索引值
    private var studentLicenseName: String = ""        //學生所填的證照名稱
    private var studentLicenseDate: String = ""        //學去所填的證照年月
    private var studentDepartment: String = ""         //存學生的系所
    private var studentJoin: String = ""               //存學生的入學年度
    private var studentAdress: String = ""             //存學生的地址
    private var studentPhone: String = ""              //存學生的電話號碼
    private var licenseType: Int =
        0                   //0 : 國家考試  1 : 政府機關 2 : 國際認證 3 : 外語認證 4 : 其他證照

    // Api Service for Course
    private val formApplyApiService by lazy {
        FormApplyApiService.create()
    }

    private var disposable: Disposable? = null

    private val editContentFragmentFromPersonalPage = EditContentFragment()

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Decide to Inflate which layout for this fragment
        return when (arguments!!.getString("TabNow")) {
            resources.getStringArray(R.array.form_tab)[0] -> {
                inflater.inflate(R.layout.fragment_form_content_apply, container, false).apply {

                    //UI finder
                    radioGrade = findViewById(R.id.newResponse_gradeRadio)//最上方 成績單 黃色單選鈕
                    radioLicense =
                        findViewById(R.id.newResponse_licenseRadio)              //最上方 證照 黃色單選鈕
                    gradeTextView =
                        findViewById(R.id.form_apply_text_view1)                //文字“成績單” 或 文字“證照”
                    gradeOrLicenseUpload =
                        findViewById(R.id.form_upload_button)            //成績單或證照 上傳用的按鈕
                    gradeBetterTextView =
                        findViewById(R.id.form_apply_text_view2)          //文字“成績進步”
                    gradeBetterExplainEditText =
                        findViewById(R.id.form_apply_edit_text1)   //解釋你成績進步多少的打字框
                    hereIsWhereUploadedImage =
                        findViewById(R.id.form_image_view)           //成績單或證照的圖片呈現的地方
                    sureEditBtn =
                        findViewById(R.id.form_sure_edit_button)                  //提交資料的按鈕
                    schoolYearTextView =
                        findViewById(R.id.schoolYearTextView)              //文字”學年度”
                    choseSchoolYearTextView =
                        findViewById((R.id.onSchoolYearBtnTextView))  //文字“請選擇學年”(長在框框裡)
                    choseSchoolYearBtn = findViewById(R.id.schoolYearBtn)                   //選學年的按鈕
                    licenseColumn =
                        findViewById(R.id.licenseColumn)                        //證照資訊的打字框們
                    licenseName =
                        findViewById(R.id.licenseName)                            //證照名稱的輸入處
                    licenseDate =
                        findViewById(R.id.licenseDate)                            //證照日期(年.月)的輸入處
                    radioGroup1 =
                        findViewById(R.id.radioButtonGroup4)                      //證照單選鈕第一排
                    radioCountry =
                        findViewById(R.id.radioCountry)                          //單選鈕“國家考試”
                    radioGovernment =
                        findViewById(R.id.radioGovernment)                    //單選鈕“政府機關“
                    radioNational =
                        findViewById(R.id.radioNational)                        //單選鈕“國際認證”
                    radioGroup2 =
                        findViewById(R.id.radioButtonGroup5)                      //證照單選鈕第二排
                    radioForeign =
                        findViewById(R.id.radioForeign)                          //單選鈕“外語認證“
                    radioElse =
                        findViewById(R.id.radioElse)                                //單選鈕“其他”
                    profileFrameLayout =
                        findViewById(R.id.form_apply_frame_holder)         //履歷的那一撮整個區塊

                    licenseColumn.visibility = View.INVISIBLE
                    radioGroup1.visibility = View.INVISIBLE
                    radioGroup2.visibility = View.INVISIBLE

                    //使用者選擇成績單頁面 將證照的部分隱藏 GONE表示不佔體積 Invisible只是看不見 會佔體積
                    radioGrade.setOnClickListener {
                        gradeTextView.text = "成績單"
                        gradeBetterTextView.visibility = View.VISIBLE
                        gradeBetterExplainEditText.visibility = View.VISIBLE
                        schoolYearTextView.visibility = View.VISIBLE
                        choseSchoolYearTextView.visibility = View.VISIBLE
                        choseSchoolYearBtn.visibility = View.VISIBLE

                        gradeTextView.visibility = View.VISIBLE
                        gradeOrLicenseUpload.visibility = View.VISIBLE
                        hereIsWhereUploadedImage.visibility = View.VISIBLE
                        profileFrameLayout.visibility = View.VISIBLE
                        sureEditBtn.visibility = View.VISIBLE

                        licenseColumn.visibility = View.GONE
                        radioGroup1.visibility = View.GONE
                        radioGroup2.visibility = View.GONE
                    }

                    //使用者選擇證照頁面 將成績單的部分隱藏 GONE表示不佔體積 Invisible只是看不見 會佔體積
                    radioLicense.setOnClickListener {
                        gradeBetterTextView.visibility = View.GONE
                        gradeBetterExplainEditText.visibility = View.GONE
                        schoolYearTextView.visibility = View.INVISIBLE
                        choseSchoolYearTextView.visibility = View.INVISIBLE
                        choseSchoolYearBtn.visibility = View.INVISIBLE
                        gradeTextView.text = "證照"
                        licenseColumn.visibility = View.VISIBLE
                        radioGroup1.visibility = View.VISIBLE
                        radioGroup2.visibility = View.VISIBLE

                        gradeTextView.visibility = View.VISIBLE
                        gradeOrLicenseUpload.visibility = View.VISIBLE
                        hereIsWhereUploadedImage.visibility = View.VISIBLE
                        profileFrameLayout.visibility = View.VISIBLE
                        sureEditBtn.visibility = View.VISIBLE

                    }

                    // Button setup
                    gradeOrLicenseUpload.setOnClickListener {
                        pickImage(gradeOrLicenseUpload)
                    }
                    sureEditBtn.setOnClickListener {
                        if (radioGrade.isChecked) sureButtonOnClick(true)
                        else sureButtonOnClick(false)
                    }
                    choseSchoolYearBtn.setOnClickListener {
                        val mDialogView = LayoutInflater.from(this.context)
                            .inflate(R.layout.dialog_choose_class, null)
                        val mBuilder = AlertDialog.Builder(this.context).setView(mDialogView)
                        val mAlertDialog = mBuilder.show()

                        numberPickers = mDialogView.takeLeaveNumberPicker
                        numberPickers.minValue = 0
                        numberPickers.maxValue = schoolYearArray.size - 1
                        numberPickers.displayedValues = schoolYearArray
                        numberPickers.wrapSelectorWheel = false

                        mDialogView.takeLeaveOkBtn.setOnClickListener {
                            if (numberPickers.value == 0) {
                                choseSchoolYearTextView.text = (schoolYearArray[0])
                                mAlertDialog.dismiss()
                                choseSchoolYearBtn.setBackgroundResource(R.drawable.yellow)
                            }
                        }

                        numberPickers.setOnValueChangedListener { _, _, newVal ->
                            mDialogView.takeLeaveOkBtn.setOnClickListener {
                                mAlertDialog.dismiss()
                                choseSchoolYearTextView.text = schoolYearArray[newVal]
                                schoolYearPick = newVal
                                //選完就變黃色
                                choseSchoolYearBtn.setBackgroundResource(R.drawable.yellow)
                            }
                        }
                    }

                    val type = arguments?.getInt("type")
                    val qrCode = arguments?.getString("qrCode")
                    val department = arguments?.getString("department")
                    val admissionDate = arguments?.getString("admissionDate")
                    val address = arguments?.getString("address")
                    val phoneNumber = arguments?.getString("phoneNumber")
                    val portrait = arguments?.getString("portrait")
                    val studentIdCard = arguments?.getString("studentIdCard")
                    val idCard = arguments?.getString("idCard")
                    val idCardBackSide = arguments?.getString("idCardBackSide")
                    val bankbook = arguments?.getString("bankbook")

                    childFragmentManager.beginTransaction()
                        .replace(
                            R.id.form_apply_frame_holder,
                            editContentFragmentFromPersonalPage.apply {

                                studentDepartment = department.toString()
                                studentJoin = admissionDate.toString()
                                studentAdress = address.toString()
                                studentPhone = phoneNumber.toString()
                                arguments = Bundle().apply {

                                    putBoolean("InForm", true)
                                    putInt("type", type!!)
                                    putString("qrCode", qrCode)
                                    putString("department", department)
                                    putString("admissionDate", admissionDate)
                                    putString("address", address)
                                    putString("phoneNumber", phoneNumber)
                                    putString("portrait", portrait)
                                    putString("studentIdCard", studentIdCard)
                                    putString("idCard", idCard)
                                    putString("idCardBackSide", idCardBackSide)
                                    putString("bankbook", bankbook)
                                }
                            })
                        .commit()
                }
            }
            resources.getStringArray(R.array.form_tab)[1] -> {
                inflater.inflate(R.layout.fragment_form_content_review, container, false).apply {
                    disposable =
                        formApplyApiService.getMyApply((activity as PushPull).studentUUID!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                val reviewList: ListView = findViewById(R.id.form_review_list)
                                val applyName: ArrayList<String> = arrayListOf()
                                val applyDate: ArrayList<String> = arrayListOf()
                                val applyState: ArrayList<Int> = arrayListOf()
                                if (myApply.isNotEmpty()) {
                                    for (i in myApply.indices) {
                                        applyName.add(myApply[i].name)
                                        applyDate.add(myApply[i].pushDate)
                                        applyState.add(myApply[i].state)
                                    }
                                    reviewList.adapter =
                                        FormReviewListAdapter(
                                            this.context,
                                            applyName,
                                            applyDate,
                                            applyState
                                        )
                                }
                            }
                            .subscribeBy(
                                onNext = {
                                    myApply = it
                                },
                                onError = {
                                    Log.e("Error", it.message)
                                },
                                onComplete = {

                                }
                            )
                }
            }
            else -> {
                inflater.inflate(R.layout.fragment_form_content_result, container, false).apply {
                    succeedShow = findViewById(R.id.succed_all)
                    failShow = findViewById(R.id.fail_all)

                    disposable =
                        formApplyApiService.getPass((activity as PushPull).studentUUID!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                val resultList: ListView = findViewById(R.id.form_result_list)
                                val passName2: ArrayList<String> = arrayListOf()
                                val passDate2: ArrayList<String> = arrayListOf()
                                val passState2: ArrayList<Int> = arrayListOf()
                                val passResult2: ArrayList<String> = arrayListOf()
                                if (myLicense.isNotEmpty()) {
                                    succeedShow.visibility = View.VISIBLE
                                    for (i in myLicense.indices) {
                                        passDate2.add(myLicense[i].verifiedDate)
                                        passState2.add(myLicense[i].state)
                                        passName2.add(myLicense[i].name)
                                        passResult2.add(myLicense[i].verificationRejectedReason)
                                        for (i in myLicense.indices){

                                        }
                                        myLicense.indices.forEach {index ->
                                            print(index)
                                            if (index == 2) {
                                                return@forEach
                                            }
                                            myLicense.indices.forEach test@{
                                                return@test
                                            }



                                        }
                                    }

                                    resultList.adapter = FormResultExpandableListAdapter(
                                        this.context,
                                        passDate2,
                                        passState2,
                                        passName2,
                                        passResult2,
                                        0
                                    )
                                }

                            }
                            .subscribeBy(
                                onNext = {
                                    myLicense = it
                                },
                                onError = {
                                    Log.e("Error", it.message)
                                }
                            )
                    disposable =
                        formApplyApiService.getDecline((activity as PushPull).studentUUID!!)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterTerminate {
                                val resultList: ListView = findViewById(R.id.form_result_list2)
                                val passName2: ArrayList<String> = arrayListOf()
                                val passDate2: ArrayList<String> = arrayListOf()
                                val passState2: ArrayList<Int> = arrayListOf()
                                val decline2: ArrayList<String> = arrayListOf()
                                if (myLicense.isNotEmpty()) {
                                    failShow.visibility = View.VISIBLE
                                    for (i in myLicense.indices) {
                                        passDate2.add(myLicense[i].verifiedDate)
                                        passState2.add(myLicense[i].state)
                                        passName2.add(myLicense[i].name)
                                        decline2.add(myLicense[i].verificationRejectedReason)
                                    }
                                    resultList.adapter = FormResultExpandableListAdapter(
                                        this.context,
                                        passDate2,
                                        passState2,
                                        passName2,
                                        decline2,
                                        1
                                    )
                                }

                            }
                            .subscribeBy(
                                onNext = {
                                    myLicense = it
                                },
                                onError = {
                                    Log.e("Error", it.message)
                                }
                            )
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (arguments!!.getString("TabNow")) {
            resources.getStringArray(R.array.form_tab)[0] -> {

                disposable = Observable.zip(
                    formApplyApiService.getGradeTime(),
                    formApplyApiService.getLicenseTime(),
                    BiFunction<FormModel.ApplyTime, FormModel.ApplyTime, Pair<Int, Int>> { t1, t2 ->
                        Pair(
                            t1.status,
                            t2.status
                        )
                    }
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(
                        onNext = { status ->
                            gradeApplyAllow = status.first
                            licenseApplyAllow = status.second
                        },
                        onComplete = {
                            Log.d(
                                TAG,
                                "GET Status success grade:${gradeApplyAllow != 1}, license:${licenseApplyAllow != 1}"
                            )
                            if (gradeApplyAllow != 1 && licenseApplyAllow != 1) {
                                // Do when both available
                            } else if (gradeApplyAllow != 1) {
                                // Do when only grade available
                                radioLicense.isClickable = false
                                radioLicense.alpha = .5f
                            } else if (licenseApplyAllow != 1) {
                                // Do when only license available
                                radioLicense.callOnClick()
                                radioLicense.isChecked = true
                                radioGrade.isChecked = false
                                radioGrade.isClickable = false
                                radioGrade.alpha = .5f
                            } else {
                                // Do when all unavailable
                                form_apply_blocker.visibility = View.VISIBLE
                                form_apply_blocker.isClickable = true
                                form_apply_blocker.isFocusable = true
                            }
                        },
                        onError = { Log.e("Error", it.message) }
                    )

                radioCountry.setOnClickListener {
                    radioForeign.isChecked = false
                    radioElse.isChecked = false
                    licenseType = 0
                }
                radioGovernment.setOnClickListener {
                    radioForeign.isChecked = false
                    radioElse.isChecked = false
                    licenseType = 1
                }
                radioNational.setOnClickListener {
                    radioForeign.isChecked = false
                    radioElse.isChecked = false
                    licenseType = 2
                }
                radioForeign.setOnClickListener {
                    radioCountry.isChecked = false
                    radioGovernment.isChecked = false
                    radioNational.isChecked = false
                    licenseType = 3
                }
                radioElse.setOnClickListener {
                    radioCountry.isChecked = false
                    radioGovernment.isChecked = false
                    radioNational.isChecked = false
                    licenseType = 4
                }
            }
            resources.getStringArray(R.array.form_tab)[1] -> {

            }
        }
    }

    //傳進來參數true表示要以成績單申請 false表示以證照申請 TODO合併sureButtonOnClick  uploadImageForGrade
    private fun sureButtonOnClick(boo1: Boolean) {
        Log.d("123", "boo1 is $boo1")
        if (isChanged.contains(true)) {
            if (isChanged[0]) {
                val bitmap = (hereIsWhereUploadedImage.drawable as BitmapDrawable).bitmap
                if (boo1) {
                    uploadImageForGrade(
                        (activity as PushPull).studentUUID!!,
                        "grade",
                        ByteArrayOutputStream1().apply {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
                        }.toByteArray()
                    )
                } else {
                    uploadImageForLicense(
                        (activity as PushPull).studentUUID!!,
                        "license",
                        ByteArrayOutputStream1().apply {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
                        }.toByteArray()
                    )
                }
            }
        }
        // TODO : Progress Bar
        activity!!.onBackPressed()
    }

    private fun uploadImageForGrade(stdId: String, imageType: String, imageBytes: ByteArray) {
        val s1 = "studentIdCard"
        val s2 = "idCard"
        val s3 = "idCardBackSide"
        val s4 = "bankbook"

        val gradeDiscribe: String = gradeBetterExplainEditText.text.toString()
        val studentIDCardByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.studentCardImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        val iDCardByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.idCardFrontImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        val iDCardBackByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.idCardBackImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        val bankbookByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.bankbookImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        disposable =
            formApplyApiService.uploadImageForGrade(
                imageType,
                RequestBody.create(MediaType.parse("text/plain"), schoolYearArray[schoolYearPick]),
                RequestBody.create(MediaType.parse("text/plain"), semesterArray[semesterPick]),
                RequestBody.create(MediaType.parse("text/plain"), gradeDiscribe),
                RequestBody.create(MediaType.parse("text/plain"), stdId),
                RequestBody.create(MediaType.parse("text/plain"), studentDepartment),
                RequestBody.create(MediaType.parse("text/plain"), studentJoin),
                RequestBody.create(MediaType.parse("text/plain"), studentAdress),
                RequestBody.create(MediaType.parse("text/plain"), studentPhone),
                MultipartBody.Part.createFormData(
                    "image",
                    "$stdId$imageType.jpg",
                    RequestBody.create(MediaType.parse("image/*"), imageBytes)
                ),
                MultipartBody.Part.createFormData(
                    "studentIdCard",
                    "$stdId$s1.jpg",
                    RequestBody.create(MediaType.parse("image/*"), studentIDCardByteArray)
                ),
                MultipartBody.Part.createFormData(
                    "idCard",
                    "$stdId$s2.jpg",
                    RequestBody.create(MediaType.parse("image/*"), iDCardByteArray)
                ),
                MultipartBody.Part.createFormData(
                    "idCardBackSide",
                    "$stdId$s3.jpg",
                    RequestBody.create(MediaType.parse("image/*"), iDCardBackByteArray)
                ),
                MultipartBody.Part.createFormData(
                    "bankbook",
                    "$stdId$s4.jpg",
                    RequestBody.create(MediaType.parse("image/*"), bankbookByteArray)
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {
                        Log.d(TAG, "Upload $imageType Succeed!!!")
//                        Toast.makeText(context,  "送出成功", Toast.LENGTH_LONG).show()
                    },
                    onError = {
                        Log.e(TAG, it.message)
                    }
                )
    }

    private fun uploadImageForLicense(stdId: String, imageType: String, imageBytes: ByteArray) {
        val s1 = "studentIdCard"
        val s2 = "idCard"
        val s3 = "idCardBackSide"
        val s4 = "bankbook"

        studentLicenseName = licenseName.text.toString()
        studentLicenseDate = licenseDate.text.toString()

        val studentIDCardByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.studentCardImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        val iDCardByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.idCardFrontImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        val iDCardBackByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.idCardBackImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()
        val bankbookByteArray: ByteArray = ByteArrayOutputStream1().apply {
            ((editContentFragmentFromPersonalPage.bankbookImageView.drawable as BitmapDrawable).bitmap).compress(
                Bitmap.CompressFormat.JPEG,
                100,
                this
            )
        }.toByteArray()

        disposable =
            formApplyApiService.uploadImageForLicense(
                imageType,
                RequestBody.create(MediaType.parse("text/plain"), studentLicenseName),
                RequestBody.create(MediaType.parse("text/plain"), licenseType.toString()),
                RequestBody.create(MediaType.parse("text/plain"), studentLicenseDate),
                RequestBody.create(MediaType.parse("text/plain"), stdId),
                RequestBody.create(MediaType.parse("text/plain"), studentDepartment),
                RequestBody.create(MediaType.parse("text/plain"), studentJoin),
                RequestBody.create(MediaType.parse("text/plain"), studentAdress),
                RequestBody.create(MediaType.parse("text/plain"), studentPhone),

                MultipartBody.Part.createFormData(
                    "image",
                    "$stdId$imageType.jpg",
                    RequestBody.create(MediaType.parse("image/*"), imageBytes)
                ),
                MultipartBody.Part.createFormData(
                    "studentIdCard",
                    "$stdId$s1.jpg",
                    RequestBody.create(MediaType.parse("image/*"), studentIDCardByteArray)
                ),
                MultipartBody.Part.createFormData(
                    "idCard",
                    "$stdId$s2.jpg",
                    RequestBody.create(MediaType.parse("image/*"), iDCardByteArray)
                ),
                MultipartBody.Part.createFormData(
                    "idCardBackSide",
                    "$stdId$s3.jpg",
                    RequestBody.create(MediaType.parse("image/*"), iDCardBackByteArray)
                ),
                MultipartBody.Part.createFormData(
                    "bankbook",
                    "$stdId$s4.jpg",
                    RequestBody.create(MediaType.parse("image/*"), bankbookByteArray)
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {
                        Log.d(TAG, "Upload $imageType Succeed!!!")
                        Toast.makeText(context, "證照申請送出成功", Toast.LENGTH_LONG).show()
                    },
                    onError = {
                        Log.e(TAG, it.message)
                    }
                )
    }

    private fun pickImage(btn: Button) {
//        Log.d("debug", "pickIm in")
//        val destIntent = Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
//        }, null)
//        startActivityForResult(
//            destIntent, when (btn) {
//                gradeOrLicenseUpload -> 123
//                else -> 123
//            }
//        )
        Toast.makeText(context, "圖片上傳功能將於未來開放", Toast.LENGTH_SHORT).show()
        Log.d("debug", "pickIm out")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("debug", "onA in")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                // Jugde Size (MegaByte)
                if ((context!!.contentResolver.openInputStream(intent.data!!)!!.available() / 1048576) <= 5) {
                    when (requestCode) {
                        123 -> {
                            isChanged[0] = true
                            hereIsWhereUploadedImage.setImageBitmap(
                                waterMark(
                                    MediaStore.Images.Media.getBitmap(
                                        context!!.contentResolver,
                                        intent.data
                                    ), 40
                                )
                            )
                        }
                    }
                } else {
                    Toast.makeText(context, "圖片大小應不大於5MB", Toast.LENGTH_LONG).show()
                }
            }
        }
        Log.d("debug", "onA out")
    }

    private fun waterMark(
        src: Bitmap,
        alpha: Int = 40,
        underline: Boolean = false
    ): Bitmap {
        val orgW = src.width
        val orgH = src.height
        var processedBitmap = src

        // Scaling the Image Avoid when Image Cannot be Drawn
        if (orgH > getOpenglRenderLimitValue() || orgW > getOpenglRenderLimitValue()) {
            val matrix = Matrix()
            if (orgH >= orgW) {
                matrix.postScale(
                    getOpenglRenderLimitValue() / orgH.toFloat(),
                    getOpenglRenderLimitValue() / orgH.toFloat()
                )
            } else {
                matrix.postScale(
                    getOpenglRenderLimitValue() / orgW.toFloat(),
                    getOpenglRenderLimitValue() / orgW.toFloat()
                )
            }

            processedBitmap = Bitmap.createBitmap(src, 0, 0, orgW, orgH, matrix, true)
        }

        val result = Bitmap.createBitmap(processedBitmap.width, processedBitmap.height, src.config)
        val canvas = Canvas(result)
        canvas.drawBitmap(processedBitmap, 0f, 0f, null)
        canvas.drawText("臺北市立大學用", 0f, processedBitmap.height.toFloat() / 2, Paint().apply {
            color = Color.BLACK
            this.alpha = alpha
            textSize = processedBitmap.width.toFloat() / 7
            isAntiAlias = true
            isUnderlineText = underline
        })
        processedBitmap.recycle()
        return result
    }

}
