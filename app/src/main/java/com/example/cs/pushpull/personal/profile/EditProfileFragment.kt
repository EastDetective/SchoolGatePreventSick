package com.example.cs.pushpull.personal.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs.pushpull.R

class EditProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = resources.getString(R.string.profile)

        // Show Back button on Top-left
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_edit, container, false).apply {
            val department = arguments?.getString("profile_department")
            val address = arguments?.getString("profile_address")
            val admissionDate = arguments?.getString("profile_admissionDate")
            val phoneNumber = arguments?.getString("profile_phoneNumber")
            val portrait = arguments?.getString("profile_portrait")
            val studentIdCard = arguments?.getString("profile_studentIdCard")
            val idCard = arguments?.getString("profile_idCard")
            val idCardBackSide = arguments?.getString("profile_idCardBackSide")
            val bankbook = arguments?.getString("profile_bankbook")
            val type = arguments?.getInt("profile_type")
            val qrCode = arguments?.getString("profile_qrCode")

            childFragmentManager.beginTransaction()
                .replace(R.id.profile_fragment_holder, EditContentFragment().also {
                    it.arguments = Bundle().apply {
                        putString("department", department)
                        putString("admissionDate", admissionDate)
                        putString("address", address)
                        putString("phoneNumber", phoneNumber)
                        putString("portrait", portrait)
                        putString("studentIdCard", studentIdCard)
                        putString("idCard", idCard)
                        putString("idCardBackSide", idCardBackSide)
                        putString("bankbook", bankbook)
                        putInt("type", type!!)
                        putString("qrCode", qrCode)
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }
}
