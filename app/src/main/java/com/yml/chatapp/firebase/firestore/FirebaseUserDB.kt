package com.yml.chatapp.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseUserDB {

    private val fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseUserDB? = null
        fun getInstance(): FirebaseUserDB = instance ?: FirebaseUserDB()
    }
}