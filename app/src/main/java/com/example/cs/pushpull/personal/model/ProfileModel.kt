package com.example.cs.pushpull.personal.model

object ProfileModel {

    data class Full(
        val studentID: String,
        val password: String,
        val studentName: String,
        val studentType: Int,
        val studentDepartment: String,
        val studentRegisterDate: String,
        val studentAddress: String,
        val studentPhoneNumber: String,
        val studentQRcode: String,
        val idCard: String,
        val idCardBackSide: String,
        val studentIdCard: String,
        val bankbook: String,
        val portrait: String,
        val studentBlackList: Int,
        val blackLastDate: String,
        val token: String,
        val pushToken: String
    )
}