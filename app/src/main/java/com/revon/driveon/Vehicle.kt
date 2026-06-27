package com.revon.driveon

data class Vehicle(

    val vehicleNumber: String = "",
    val bikeName: String = "",
    val status: String = "",         // NEW

    val day1Price: String = "",
    val day2Price: String = "",
    val day7Price: String = "",
    val day30Price: String = "",

    val day1KmLimit: String = "",
    val day2KmLimit: String = "",
    val day7KmLimit: String = "",
    val day30KmLimit: String = "",

    val day1ExtraKm: String = "",
    val day2ExtraKm: String = "",
    val day7ExtraKm: String = "",
    val day30ExtraKm: String = "",

    val available: Boolean = true
)