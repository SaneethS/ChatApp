package com.yml.chatapp.ui.wrapper

import java.io.Serializable

data class User(
    val phoneNo: String,
    val fUid: String = "",
    var name: String = "",
    var status: String = "",
    val isNewUser: Boolean = false
): Serializable
