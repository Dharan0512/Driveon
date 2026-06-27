package com.revon.driveon

data class Booking(

    val customDays: String = "",
    val customPrice: String = "",
    val customRemarks: String = "",

    val bookingId: String = "",
    val userPhone: String = "",

    val vehicleNumber: String = "",
    val bikeName: String = "",

    val packageType: String = "",
    val packageCount: String = "",

    val rent: String = "",

    val startReading: String = "",
    val endReading: String = "",

    val agreedPrice: String = "",
    val kmLimit: String = "",
    val extraKmCharge: String = "",
    val startPhotoUrl: String = "",
    val endPhotoUrl: String = "",

    val date: String = "",

    val generatedBill: String = "",
    val finalBill: String = "",

    val adminRemarks: String = "",

    val status: String = ""
)