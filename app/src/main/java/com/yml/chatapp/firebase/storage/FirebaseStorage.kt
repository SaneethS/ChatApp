package com.yml.chatapp.firebase.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.yml.chatapp.common.USERS
import com.yml.chatapp.firebase.auth.Authentication
import kotlin.coroutines.suspendCoroutine

object FirebaseStorage {
    private var storage = Firebase.storage.reference
    private var image = storage.child("profileImages")

    suspend fun setUserProfile(profile: Uri): Uri {
        val userId = Authentication.getCurrentUser()?.uid.toString()
        return suspendCoroutine { callback ->
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

}