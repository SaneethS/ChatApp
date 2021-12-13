package com.yml.chatapp.data.model

data class DbUser(
    val phoneNo: String,
    val name: String = "",
    val status: String = "",
    val image: String = "",
    val firebaseToken: String = ""
)
