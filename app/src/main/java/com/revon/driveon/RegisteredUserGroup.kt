package com.revon.driveon

data class RegisteredUser(
    val phone: String,
    val name: String,
    val status: String = "Approved"
)

data class RegisteredUserGroup(
    val title: String,
    val subtitle: String,
    val count: Int,
    var expanded: Boolean = false,
    val users: MutableList<RegisteredUser>
)