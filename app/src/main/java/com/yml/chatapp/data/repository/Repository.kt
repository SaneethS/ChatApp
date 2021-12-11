package com.yml.chatapp.data.repository

import android.net.Uri
import com.yml.chatapp.common.CONTENT_IMAGE
import com.yml.chatapp.common.CONTENT_TEXT
import com.yml.chatapp.firebase.firestore.FirebaseChatDB
import com.yml.chatapp.firebase.firestore.FirebaseGroupDB
import com.yml.chatapp.firebase.storage.FirebaseStorage
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.lang.Exception

class Repository {

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

    suspend fun sendTextToDb(senderId: String, receiverId: String, message:String):Boolean {
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
                false
            }
        }
    }

    suspend fun sendImageToDb(senderId: String, receiverId: String, Image: ByteArray):Boolean {
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
                false
            }
        }
    }

    suspend fun sendGroupTextInDb( senderId: String, message: String, group: Group): Boolean {
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
                false
            }
        }
    }

    suspend fun sendGroupImageInDb(senderId: String, image: ByteArray, group: Group): Boolean {
        return withContext(Dispatchers.IO) {
            val image = FirebaseStorage.uploadGroupImages(group, image)
            val dbMessage = Message(
                senderId,
                image,
                CONTENT_IMAGE,
                System.currentTimeMillis()
            )
            FirebaseGroupDB.getInstance().sendGroupMessageInDb(dbMessage, group)
        }
    }
}