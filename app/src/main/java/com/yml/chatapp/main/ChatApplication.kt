package com.yml.chatapp.main

import android.app.Application
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.firebase.messaging.FirebaseMessaging

class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging().getToken {
            SharedPref.getInstance(this@ChatApplication).addToken(it)
        }
    }
}