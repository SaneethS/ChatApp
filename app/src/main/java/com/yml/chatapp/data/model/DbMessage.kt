package com.yml.chatapp.data.model

import java.util.*

data class DbMessage (
    val senderId: String,
    val receiverId: String,
    val content: String,
    val contentType: String,
    val dateCreated: Long
)