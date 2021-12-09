package com.yml.chatapp.ui.wrapper

data class Message (
    val senderId: String,
    val receiverId: String,
    val content: String,
    val contentType: String,
    val dateCreated: Long
)
