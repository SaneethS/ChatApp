package com.yml.chatapp.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.USERS
import com.yml.chatapp.common.Util
import com.yml.chatapp.data.model.DbUser
import com.yml.chatapp.firebase.auth.Authentication
import com.yml.chatapp.ui.wrapper.User

class FirebaseUserDB {

    private var fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseUserDB? = null
        fun getInstance(): FirebaseUserDB = instance ?: FirebaseUserDB()
    }

    fun setUserToDb(user: User, callback: (Boolean) -> Unit) {
        val userDetails:DbUser = if(user.name.isEmpty()){
            DbUser(user.phoneNo, user.phoneNo)
        }else{
            DbUser(user.phoneNo, user.name)
        }

        fireStore.collection(USERS).document(user.fUid).set(userDetails).addOnCompleteListener {
            if(it.isSuccessful) {
                callback(true)
            }else {
                callback(false)
            }
        }
    }

    fun getUserFromDb(userId:String, callback: (User?) -> Unit) {
        fireStore.collection(USERS).document(userId).get()
            .addOnCompleteListener {    task ->
                if(task.isSuccessful) {
                    task.result.also {
                        val dbUser = Util.userInfoFromHashMap(it?.data as HashMap<*, *>)
                        val user = User(phoneNo = dbUser.phoneNo,
                                    fUid = userId,
                                    name = dbUser.name)
                        callback(user)
                    }
                }else{
                    callback(null)
                }
            }
    }

    fun updateUserInDb(user: User, callback: (Boolean) -> Unit) {
        val userMap = mapOf(
            "name" to user.name
        )

        fireStore.collection(USERS).document(user.fUid).update(userMap)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    callback(true)
                }else{
                    callback(false)
                }
            }
    }
}