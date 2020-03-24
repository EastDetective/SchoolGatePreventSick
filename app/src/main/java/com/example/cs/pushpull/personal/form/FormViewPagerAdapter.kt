package com.example.cs.pushpull.personal.form

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.cs.pushpull.personal.model.ProfileModel

class FormViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val tabs: Array<String>,
    private val userData: ProfileModel.Full?
) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getItem(p0: Int) = FormContentFragment().apply {
        arguments = Bundle().apply {

            // Put Current Tab's Name
            putString("TabNow", getPageTitle(p0))

            userData?.run {
                putInt("type", userData.studentType)
                putString("qrCode", userData.studentQRcode)
                putString("department", userData.studentDepartment)
                putString("admissionDate", userData.studentRegisterDate)
                putString("address", userData.studentAddress)
                putString("phoneNumber", userData.studentPhoneNumber)
                putString("portrait", userData.portrait)
                putString("studentIdCard", userData.studentIdCard)
                putString("idCard", userData.idCard)
                putString("idCardBackSide", userData.idCardBackSide)
                putString("bankbook", userData.bankbook)
            }
        }
    }

    override fun getCount() = tabs.size

    override fun getPageTitle(position: Int) = tabs[position % tabs.size]

}