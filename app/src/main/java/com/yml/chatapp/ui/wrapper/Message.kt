package com.yml.chatapp.ui.wrapper

data class Message (
    val senderId: String,
    val content: String,
    val contentType: String,
    val dateCreated: Long
)
