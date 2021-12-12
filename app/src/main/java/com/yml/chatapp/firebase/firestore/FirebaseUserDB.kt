package com.yml.chatapp.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.USERS
import com.yml.chatapp.common.Util
import com.yml.chatapp.data.model.DbUser
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.suspendCoroutine

class FirebaseUserDB {

    private var fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseUserDB? = null
        fun getInstance(): FirebaseUserDB = instance ?: FirebaseUserDB()
    }

    suspend fun setUserToDb(user: User): Boolean {
        val userDetails: DbUser = if (user.name.isEmpty()) {
            DbUser(user.phoneNo, user.phoneNo, user.status, user.image)
        } else {
            DbUser(user.phoneNo, user.name, user.status, user.image)
        }
        return suspendCoroutine { callback ->
            fireStore.collection(USERS).document(user.fUid).set(userDetails).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.resumeWith(Result.success(true))
                } else {
                    callback.resumeWith(
                        Result.failure(
                            it.exception ?: Exception("Something went wrong")
                        )
                    )
                }
            }
        }
    }

    suspend fun getUserFromDb(userId: String): User? {
        return suspendCoroutine { callback ->
            fireStore.collection(USERS).document(userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.also {
                            val dbUser = Util.userInfoFromHashMap(it?.data as HashMap<*, *>)
                            val user = User(
                                phoneNo = dbUser.phoneNo,
                                fUid = userId,
                                name = dbUser.name,
                                status = dbUser.status,
                                image = dbUser.image
                            )
                            callback.resumeWith(Result.success(user))
                        }
                    } else {
                        callback.resumeWith(
                            Result.failure(
                                task.exception ?: Exception("Something went wrong")
                            )
                        )
                    }
                }
        }
    }

    suspend fun updateUserInDb(user: User): Boolean {
        val userMap = mapOf(
            "name" to user.name,
            "status" to user.status,
            "image" to  user.image
        )
        return suspendCoroutine { callback ->
            fireStore.collection(USERS).document(user.fUid).update(userMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(true))
                    } else {
                        callback.resumeWith(
                            Result.failure(
                                it.exception ?: Exception("Something went wrong")
                            )
                        )
                    }
                }
        }
    }

    suspend fun getUserListFromDb(): ArrayList<User>? {
        return suspendCoroutine { callback ->
            fireStore.collection(USERS).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList = ArrayList<User>()
                    val dataSnapshot = task.result

                    if (dataSnapshot != null) {
                        for (item in dataSnapshot.documents) {
                            val userHashMap = item.data as HashMap<*, *>
                            val user = User(
                                name = userHashMap["name"].toString(),
                                phoneNo = userHashMap["phoneNo"].toString(),
                                status = userHashMap["status"].toString(),
                                fUid = item.id,
                                image = userHashMap["image"].toString()
                            )
                            userList.add(user)
                        }
                        callback.resumeWith(Result.success(userList))
                    }
                }else {
                    callback.resumeWith(
                        Result.failure(
                            task.exception ?: Exception("Something went wrong")
                        )
                    )
                }
            }
        }
    }
}