package com.yml.chatapp.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.USERS
import com.yml.chatapp.common.Util
import com.yml.chatapp.data.model.DbUser
import com.yml.chatapp.ui.wrapper.User
import kotlin.coroutines.suspendCoroutine

class FirebaseUserDB {

    private var fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseUserDB? = null
        fun getInstance(): FirebaseUserDB = instance ?: FirebaseUserDB()
    }

    fun setUserToDb(user: User, callback: (Boolean) -> Unit) {
        val userDetails:DbUser = if(user.name.isEmpty()){
            DbUser(user.phoneNo, user.phoneNo, user.status)
        }else{
            DbUser(user.phoneNo, user.name, user.status)
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
                                    name = dbUser.name,
                                    status = dbUser.status)
                        callback(user)
                    }
                }else{
                    callback(null)
                }
            }
    }

    fun updateUserInDb(user: User, callback: (Boolean) -> Unit) {
        val userMap = mapOf(
            "name" to user.name,
            "status" to user.status
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

    fun getUserListFromDb(callback: (ArrayList<User>?) -> Unit) {
        fireStore.collection(USERS).get().addOnCompleteListener { task->
            if(task.isSuccessful) {
                val userList = ArrayList<User>()
                val dataSnapshot = task.result

                if (dataSnapshot != null) {
                    for(item in  dataSnapshot.documents){
                        val userHashMap = item.data as HashMap<*,*>
                        val user = User(
                            name = userHashMap["name"].toString(),
                            phoneNo = userHashMap["phoneNo"].toString(),
                            status = userHashMap["status"].toString(),
                            fUid = item.id
                        )
                        userList.add(user)
                    }
                    callback(userList)
                }else {
                    callback(null)
                }
            }
        }
    }
}