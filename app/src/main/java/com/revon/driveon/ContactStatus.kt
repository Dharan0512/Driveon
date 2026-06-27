package com.revon.driveon

data class ContactStatus(
    val name: String,
    val phone: String,
    val isInApp: Boolean,
    var isSelected: Boolean = false
)