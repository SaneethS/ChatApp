package com.yml.chatapp.network

data class PushMessage(
    val to: String,
    val notification: PushContent
)
