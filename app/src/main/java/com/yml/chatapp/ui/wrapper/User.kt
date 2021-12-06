package com.yml.chatapp.ui.wrapper

data class User(
    val phoneNo: String,
    val fUid: String = "",
    var name: String = "",
    val isNewUser: Boolean = false
)
