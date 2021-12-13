package com.yml.chatapp.data.repository

import android.util.Log
import com.yml.chatapp.common.CONTENT_IMAGE
import com.yml.chatapp.common.CONTENT_TEXT
import com.yml.chatapp.common.FIREBASE_TOKEN
import com.yml.chatapp.firebase.auth.Authentication
import com.yml.chatapp.firebase.firestore.FirebaseChatDB
import com.yml.chatapp.firebase.firestore.FirebaseGroupDB
import com.yml.chatapp.firebase.firestore.FirebaseUserDB
import com.yml.chatapp.firebase.storage.FirebaseStorage
import com.yml.chatapp.network.PushContent
import com.yml.chatapp.network.PushMessage
import com.yml.chatapp.network.PushNotificationSenderService
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    private var pushNotificationSenderService: PushNotificationSenderService = PushNotificationSenderService()

    companion object {
        private val instance: Repository? = null
        fun getInstance(): Repository = instance ?: Repository()
    }

    fun chatId(senderId: String, receiverId: String): String {
        if(senderId > receiverId) {
            return senderId+"_"+receiverId
        }else {
            return receiverId+"_"+senderId
        }
    }

    suspend fun sendTextToDb(senderId: String, receiverId: String, message:String):Message? {
        return withContext(Dispatchers.IO) {
            val chatId = chatId(senderId, receiverId)
            try {
                val dbMessage = Message(
                                senderId,
                                message,
                                CONTENT_TEXT,
                                System.currentTimeMillis())
                FirebaseChatDB.getInstance().sendMessageToDb(chatId, senderId, receiverId, dbMessage)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun sendImageToDb(senderId: String, receiverId: String, Image: ByteArray):Message? {
        return withContext(Dispatchers.IO) {
            val chatId = chatId(senderId, receiverId)
            try {
                val image = FirebaseStorage.uploadChatImages(chatId, Image)
                val dbMessage = Message(
                    senderId,
                    image,
                    CONTENT_IMAGE,
                    System.currentTimeMillis())
                FirebaseChatDB.getInstance().sendMessageToDb(chatId, senderId, receiverId, dbMessage)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun sendGroupTextInDb( senderId: String, message: String, group: Group): Message? {
        return withContext(Dispatchers.IO) {
            try {
                val dbMessage = Message(
                    senderId,
                    message,
                    CONTENT_TEXT,
                    System.currentTimeMillis()
                )
                FirebaseGroupDB.getInstance().sendGroupMessageInDb(dbMessage, group)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun sendGroupImageInDb(senderId: String, image: ByteArray, group: Group): Message? {
        return withContext(Dispatchers.IO) {
            try {
                val image = FirebaseStorage.uploadGroupImages(group, image)
                val dbMessage = Message(
                    senderId,
                    image,
                    CONTENT_IMAGE,
                    System.currentTimeMillis()
                )
                FirebaseGroupDB.getInstance().sendGroupMessageInDb(dbMessage, group)
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun sendPushNotification(token: String, title: String, message: String, imageUrl: String) {
        return withContext(Dispatchers.IO) {
            val pushContent = PushContent(title, message, imageUrl)
            Log.i("Repository", "$pushContent")
            val pushMessage = PushMessage(token, pushContent)
            Log.i("Repository", "${pushNotificationSenderService.sendPushNotification(pushMessage)}")
        }
    }

    suspend fun sendGroupNotification(members: ArrayList<String>, title: String, message: String, imageUrl: String) {
        return withContext(Dispatchers.IO) {
            for(i in members) {
                sendPushNotification(i, title, message, imageUrl)
            }
        }
    }

    suspend fun logout(userId: String) {
        return withContext(Dispatchers.IO) {
            val userToken = mapOf(
                FIREBASE_TOKEN to ""
            )
            FirebaseUserDB.getInstance().updateUserToken(userId, userToken)
            Authentication.signOut()
        }
    }
}