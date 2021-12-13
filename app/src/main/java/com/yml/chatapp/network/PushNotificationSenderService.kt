package com.yml.chatapp.network

import com.yml.chatapp.common.API_KEY
import com.yml.chatapp.network.PushNotificationApi.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PushNotificationSenderService {

    suspend fun sendPushNotification(message: PushMessage): PushResponse {
        val api = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create<PushNotificationApi>()

        return  api.sendPushNotification(
            API_KEY,
            message
        )
    }
}