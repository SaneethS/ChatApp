package com.yml.chatapp.data.model

import com.yml.chatapp.ui.wrapper.User

data class DbGroup(
    val groupName: String,
    val image: String,
    val participants: ArrayList<String>
)
