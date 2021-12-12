package com.yml.chatapp.firebase.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.yml.chatapp.common.CHATS
import com.yml.chatapp.common.GROUPS
import com.yml.chatapp.common.USERS
import com.yml.chatapp.firebase.auth.Authentication
import com.yml.chatapp.ui.wrapper.Group
import java.util.*
import kotlin.coroutines.suspendCoroutine

object FirebaseStorage {
    private var storage = Firebase.storage.reference

    suspend fun setUserProfile(profile: Uri): Uri {
        val userId = Authentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
            var image = storage.child("profileImages")
            val userImage =
                image.child(USERS).child(userId).child("profile.webp")

            val upload = userImage.putFile(profile)

            upload.addOnCompleteListener {  task ->
                if(task.isSuccessful) {
                    userImage.downloadUrl.addOnSuccessListener {
                        callback.resumeWith(Result.success(it))
                    }
                }else {
                    callback.resumeWith(Result.failure(task.exception ?:
                        Exception("Something went wrong")))
                }
            }
        }
    }

    suspend fun uploadChatImages(chatId: String, image: ByteArray): String {
        val uid = UUID.randomUUID().toString()
        val ref = storage.child(CHATS).child(chatId).child(uid)
        return suspendCoroutine { callback ->
            ref.putBytes(image)
                .addOnSuccessListener { task ->
                    task.metadata?.let {
                        ref.downloadUrl.addOnSuccessListener {
                            callback.resumeWith(Result.success(it.toString()))
                        }
                    }
                }.addOnFailureListener {
                    callback.resumeWith(Result.failure(it))
                }
        }
    }

    suspend fun uploadGroupImages(group: Group, image: ByteArray): String {
        val uid = UUID.randomUUID().toString()
        val ref = storage.child(GROUPS).child(group.groupId).child(uid)
        return suspendCoroutine { callback ->
            ref.putBytes(image)
                .addOnSuccessListener { task ->
                    task.metadata?.let {
                        ref.downloadUrl.addOnSuccessListener {
                            callback.resumeWith(Result.success(it.toString()))
                        }
                    }
                }.addOnFailureListener {
                    callback.resumeWith(Result.failure(it))
                }
        }
    }

}