package com.yml.chatapp.ui.wrapper

import java.util.*

data class Message (
    val senderId: String,
    val receiverId: String,
    val content: String,
    val contentType: String,
    val dateCreated: Long
)
