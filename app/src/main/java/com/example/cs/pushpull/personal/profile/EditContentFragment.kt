package com.example.cs.pushpull.personal.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.cs.pushpull.PushPull
import com.example.cs.pushpull.R
import com.example.cs.pushpull.extension.formatTo
import com.example.cs.pushpull.extension.toDate
import com.example.cs.pushpull.extension.toISO8601UTC
import com.example.cs.pushpull.personal.PersonalApiService
import com.example.cs.pushpull.personal.model.ProfileModel
import com.github.abdularis.civ.CircleImageView
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.*

class EditContentFragment : Fragment() {

    private companion object {
        const val STICKER = 1
        const val STD_CARD = 2
        const val ID_CARD_FRONT = 3
        const val ID_CARD_BACK = 4
        const val BANKBOOK = 5
        const val TAG = "Edit"
    }

    // UI Nodes
    private lateinit var sticker: RelativeLayout
    private lateinit var sureEditBtn: Button
    private lateinit var stickerUpload: Button
    private lateinit var stdCardUpload: Button
    private lateinit var idCardUploadFront: Button
    private lateinit var idCardUploadBack: Button
    private lateinit var bankbookUpload: Button
    private lateinit var name: TextView
    private lateinit var studentID: TextView
    lateinit var department: EditText
        private set
    lateinit var address: EditText
        private set
    lateinit var admissionDate: EditText
        private set
    lateinit var phoneNumber: EditText
        private set
    private lateinit var stickerImageView: CircleImageView
    lateinit var studentCardImageView: ImageView
        private set
    lateinit var idCardFrontImageView: ImageView
        private set
    lateinit var idCardBackImageView: ImageView
        private set
    lateinit var bankbookImageView: ImageView
        private set

    // Api Service for Course
    private val personalApiService by lazy {
        PersonalApiService.create()
    }
    private var disposable: Disposable? = null

    var isChanged = arrayOf(false, false, false, false, false)

    @SuppressLint("PrivateResource", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit_content, container, false).apply {

            // Node Binding
            sticker = findViewById(R.id.profile_edit_sticker)                           //進入編輯個資的按鈕
            stickerUpload = findViewById(R.id.profile_sticker_upload_button)            //變更頭貼的按鈕
            name = findViewById(R.id.profile_name)                                      //姓名 的 打字框
            studentID = findViewById(R.id.profile_student_id)                           //學號 的 打字框
            department = findViewById(R.id.profile_department)                          //系所 的 打字框
            admissionDate = findViewById(R.id.profile_admission_date)                   //入學日期 的 打字框
            address = findViewById(R.id.profile_address)                                //地址 的 打字框
            phoneNumber = findViewById(R.id.profile_phone_number)                       //手機 的 打字框
            stdCardUpload =
                findViewById(R.id.profile_student_card_upload_button)       //上傳學生證圖片 的按鈕
            idCardUploadFront =
                findViewById(R.id.profile_id_card_front_upload_button)  //上傳身分證正面圖片 的按鈕
            idCardUploadBack =
                findViewById(R.id.profile_id_card_back_upload_button)    //上傳身分證背面圖片 的按鈕
            bankbookUpload = findViewById(R.id.profile_bankbook_upload_button)          //上傳存摺圖片 的按鈕
            stickerImageView = findViewById(R.id.profile_sticker_image_view)            //呈現頭貼圖
            studentCardImageView = findViewById(R.id.profile_student_card_image_view)   //呈現學生證圖片
            idCardFrontImageView = findViewById(R.id.profile_id_card_front_image_view)  //呈現身分證正面圖片
            idCardBackImageView = findViewById(R.id.profile_id_card_back_image_view)    //呈現身分證背面圖片
            bankbookImageView = findViewById(R.id.profile_bankbook_image_view)          //呈現存摺圖片
            sureEditBtn = findViewById(R.id.profile_sure_edit_button)                   //確認送出 的按鈕

            name.text = (activity as PushPull).studentName      //將名字設定為Push Pull直接可以拿到的學生名字
            studentID.text = (activity as PushPull).studentID   //將學號設定為Push Pull直接可以拿到的學生學號

            admissionDate.setOnClickListener {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                val month = Calendar.getInstance().get(Calendar.MONTH)
                val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { _, yearSelected, monthOfYear, _ ->

                        // Display Selected date in textbox
                        admissionDate.setText(
                            "$yearSelected.${monthOfYear + 1}",
                            TextView.BufferType.EDITABLE
                        )
                    },
                    year,
                    month,
                    day
                ).show()


            }

            arguments?.run {

                department.setText(getString("department"), TextView.BufferType.EDITABLE)
                admissionDate.setText(
                    getString("admissionDate")?.toDate()?.formatTo("yyyy.MM"),
                    TextView.BufferType.EDITABLE
                )
                address.setText(getString("address"), TextView.BufferType.EDITABLE)
                phoneNumber.setText(getString("phoneNumber"), TextView.BufferType.EDITABLE)

                getString("portrait")?.takeIf { it.isNotEmpty() }?.run {
                    Picasso.get().load(getString("portrait")).fit().centerInside()
                        .into(stickerImageView)
                }
                getString("studentIdCard")?.takeIf { it.isNotEmpty() }?.run {
                    Picasso.get().load(getString("studentIdCard")).fit().centerInside()
                        .into(studentCardImageView)
                }
                getString("idCard")?.takeIf { it.isNotEmpty() }?.run {
                    Picasso.get().load(getString("idCard")).fit().centerInside()
                        .into(idCardFrontImageView)
                }
                getString("idCardBackSide")?.takeIf { it.isNotEmpty() }?.run {
                    Picasso.get().load(getString("idCardBackSide")).fit().centerInside()
                        .into(idCardBackImageView)
                }
                getString("bankbook")?.takeIf { it.isNotEmpty() }?.run {
                    Picasso.get().load(getString("bankbook")).fit().centerInside()
                        .into(bankbookImageView)
                }
            }

            // Check if this is in Form Fragment 如果是Form頁面就做以下事情
            arguments?.run {
                if (getBoolean("InForm")) {
                    sticker.visibility = View.GONE
                    sureEditBtn.visibility = View.GONE
                    stdCardUpload.visibility = View.GONE
                    idCardUploadFront.visibility = View.GONE
                    idCardUploadBack.visibility = View.GONE
                    bankbookUpload.visibility = View.GONE
                }
            }

            // Button setup
            stickerUpload.setOnClickListener {
                pickImage(stickerUpload)
            }
            stdCardUpload.setOnClickListener {
                pickImage(stdCardUpload)
            }
            idCardUploadFront.setOnClickListener {
                pickImage(idCardUploadFront)
            }
            idCardUploadBack.setOnClickListener {
                pickImage(idCardUploadBack)
            }
            bankbookUpload.setOnClickListener {
                pickImage(bankbookUpload)
            }
            sureEditBtn.setOnClickListener {
                sureButtonOnClick()
            }
        }
    }

    fun sureButtonOnClick() {
//        if (isChanged.contains(true)) {
//            if (isChanged[0]) {
//                val bitmap = (stickerImageView.drawable as BitmapDrawable).bitmap
//                uploadImage(
//                    (activity as PushPull).studentID!!,
//                    "portrait",
//                    ByteArrayOutputStream().apply {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
//                    }.toByteArray()
//                )
//            }
//            if (isChanged[1]) {
//                val bitmap = (studentCardImageView.drawable as BitmapDrawable).bitmap
//                uploadImage(
//                    (activity as PushPull).studentID!!,
//                    "studentIdCard",
//                    ByteArrayOutputStream().apply {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
//                    }.toByteArray()
//                )
//            }
//            if (isChanged[2]) {
//                val bitmap = (idCardFrontImageView.drawable as BitmapDrawable).bitmap
//                uploadImage(
//                    (activity as PushPull).studentID!!,
//                    "idCard",
//                    ByteArrayOutputStream().apply {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
//                    }.toByteArray()
//                )
//            }
//            if (isChanged[3]) {
//                val bitmap = (idCardBackImageView.drawable as BitmapDrawable).bitmap
//                uploadImage(
//                    (activity as PushPull).studentID!!,
//                    "idCardBackSide",
//                    ByteArrayOutputStream().apply {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
//                    }.toByteArray()
//                )
//            }
//            if (isChanged[4]) {
//                val bitmap = (bankbookImageView.drawable as BitmapDrawable).bitmap
//                uploadImage(
//                    (activity as PushPull).studentID!!,
//                    "bankbook",
//                    ByteArrayOutputStream().apply {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
//                    }.toByteArray()
//                )
//            }
//        }

        if (department.text.toString() != arguments?.getString("department") ||
            admissionDate.text.toString() != arguments?.getString("admissionDate") ||
            address.text.toString() != arguments?.getString("address") ||
            phoneNumber.text.toString() != arguments?.getString("phoneNumber")
        ) {
            val pushPull = (activity as PushPull)
            disposable = personalApiService.updateUserData(
                (activity as PushPull).studentUUID!!, ProfileModel.Full(
                    pushPull.studentID!!,
                    pushPull.personalData!!.password,
                    pushPull.studentName!!,
                    pushPull.personalData!!.studentType,
                    if (department.text.toString() != "") department.text.toString() else pushPull.personalData!!.studentDepartment,
                    if (admissionDate.text.toString() != "") admissionDate.text.toString().toDate("yyyy.MM").toISO8601UTC() else pushPull.personalData!!.studentRegisterDate,
                    if (address.text.toString() != "") address.text.toString() else pushPull.personalData!!.studentAddress,
                    if (phoneNumber.text.toString() != "") phoneNumber.text.toString() else pushPull.personalData!!.studentPhoneNumber,
                    pushPull.personalData!!.studentQRcode,
                    arguments?.getString("idCard") ?: pushPull.personalData!!.idCard,
                    arguments?.getString("idCardBackSide")
                        ?: pushPull.personalData!!.idCardBackSide,
                    arguments?.getString("studentIdCard") ?: pushPull.personalData!!.studentIdCard,
                    arguments?.getString("bankbook") ?: pushPull.personalData!!.bankbook,
                    arguments?.getString("portrait") ?: pushPull.personalData!!.portrait,
                    pushPull.personalData!!.studentBlackList,
                    pushPull.personalData!!.blackLastDate,
                    pushPull.personalData!!.token,
                    pushPull.personalData!!.pushToken
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onComplete = {
                        Log.d(TAG, "Put Succeed!")
                    },
                    onError = {
                        Log.e(TAG, it.message)
                    }
                )

            // TODO : Progress Bar
            activity!!.onBackPressed()
        }
    }

    private fun pickImage(btn: Button) {
        Toast.makeText(context, "圖片上傳功能將於未來開放", Toast.LENGTH_SHORT).show()
//        val destIntent = Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).apply {
//            type = "image/*"
//            putExtra(Intent.EXTRA_LOCAL_ONLY, true)
//        }, null)
//
//        startActivityForResult(
//            destIntent, when (btn) {
//                stickerUpload -> STICKER
//                stdCardUpload -> STD_CARD
//                idCardUploadFront -> ID_CARD_FRONT
//                idCardUploadBack -> ID_CARD_BACK
//                else -> BANKBOOK
//            }
//        )
    }

    // TODO : Reopen in Future : Image Upload
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            data?.let { intent ->
//                // Jugde Size (MegaByte)
//                if ((context!!.contentResolver.openInputStream(intent.data!!)!!.available() / 1048576) <= 5) {
//                    when (requestCode) {
//                        STICKER -> {
//                            isChanged[0] = true
//                            Picasso.get().load(intent.data!!).fit().centerInside()
//                                .into(stickerImageView)
//                        }
//                        STD_CARD -> {
//                            isChanged[1] = true
//                            studentCardImageView.setImageBitmap(
//                                waterMark(
//                                    MediaStore.Images.Media.getBitmap(
//                                        context!!.contentResolver,
//                                        intent.data
//                                    ), 40
//                                )
//                            )
//                        }
//                        ID_CARD_FRONT -> {
//                            isChanged[2] = true
//                            idCardFrontImageView.setImageBitmap(
//                                waterMark(
//                                    MediaStore.Images.Media.getBitmap(
//                                        context!!.contentResolver,
//                                        intent.data
//                                    ), 40
//                                )
//                            )
//                        }
//                        ID_CARD_BACK -> {
//                            isChanged[3] = true
//                            idCardBackImageView.setImageBitmap(
//                                waterMark(
//                                    MediaStore.Images.Media.getBitmap(
//                                        context!!.contentResolver,
//                                        intent.data
//                                    ), 40
//                                )
//                            )
//                        }
//                        BANKBOOK -> {
//                            isChanged[4] = true
//                            bankbookImageView.setImageBitmap(
//                                waterMark(
//                                    MediaStore.Images.Media.getBitmap(
//                                        context!!.contentResolver,
//                                        intent.data
//                                    ), 40
//                                )
//                            )
//                        }
//                    }
//                } else {
//                    Toast.makeText(context, "圖片大小應不大於5MB", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

//    private fun waterMark(
//        src: Bitmap,
//        alpha: Int = 40,
//        underline: Boolean = false
//    ): Bitmap {
//        val orgW = src.width
//        val orgH = src.height
//        var processedBitmap = src
//
//        // Scaling the Image Avoid when Image Cannot be Drawn
//        if (orgH > getOpenglRenderLimitValue() || orgW > getOpenglRenderLimitValue()) {
//            val matrix = Matrix()
//            if (orgH >= orgW) {
//                matrix.postScale(
//                    getOpenglRenderLimitValue() / orgH.toFloat(),
//                    getOpenglRenderLimitValue() / orgH.toFloat()
//                )
//                Log.d(TAG, "${getOpenglRenderLimitValue() / orgH.toFloat()}")
//            } else {
//                matrix.postScale(
//                    getOpenglRenderLimitValue() / orgW.toFloat(),
//                    getOpenglRenderLimitValue() / orgW.toFloat()
//                )
//                Log.d(TAG, "${getOpenglRenderLimitValue() / orgW.toFloat()}")
//            }
//
//            processedBitmap = Bitmap.createBitmap(src, 0, 0, orgW, orgH, matrix, true)
//        }
//
//        val result = Bitmap.createBitmap(processedBitmap.width, processedBitmap.height, src.config)
//        val canvas = Canvas(result)
//        canvas.drawBitmap(processedBitmap, 0f, 0f, null)
//        canvas.drawText("臺北市立大學用", 0f, processedBitmap.height.toFloat() / 2, Paint().apply {
//            color = Color.BLACK
//            this.alpha = alpha
//            textSize = processedBitmap.width.toFloat() / 7
//            isAntiAlias = true
//            isUnderlineText = underline
//        })
//        processedBitmap.recycle()
//        return result
//    }

//    private fun uploadImage(stdId: String, imageType: String, imageBytes: ByteArray) {
//        disposable =
//            personalApiService.uploadImage(
//                imageType,
//                RequestBody.create(MediaType.parse("text/plain"), stdId),
//                MultipartBody.Part.createFormData(
//                    "image",
//                    "$stdId$imageType.jpg",
//                    RequestBody.create(MediaType.parse("image/*"), imageBytes)
//                )
//            )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeBy(
//                    onComplete = {
//                        Log.d(TAG, "Upload $imageType Succeed!!!")
//                    },
//                    onError = {
//                        Log.e(TAG, it.message)
//                    }
//                )
//    }


//    TODO : Avoid Rotation
//    fun getOrientation(selectedImage: Uri): Int {
//        var orientation = 0
//        val projection = arrayOf(MediaStore.Images.Media.ORIENTATION)
//        val cursor = context?.contentResolver?.query(selectedImage, projection, null, null, null)
//        cursor?.run {
//            val orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)
//            if (cursor.moveToFirst()) orientation =
//                if (cursor.isNull(orientationColumnIndex)) 0 else cursor.getInt(
//                    orientationColumnIndex
//                )
//            cursor.close()
//        }
//        return orientation
//    }
}