package com.yml.chatapp.firebase.firestore

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.*
import com.yml.chatapp.data.model.DbUser
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
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

    suspend fun addUserTokenToDb(userId: String, token: String) {
        val userMap = mapOf(
            FIREBASE_TOKEN to token
        )
        return suspendCoroutine { callback ->
            fireStore.collection(USERS).document(userId).update(userMap)
                .addOnFailureListener {
                    callback.resumeWith(Result.failure(it))
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
                                image = dbUser.image,
                                firebaseToken = dbUser.firebaseToken
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
            USER_NAME to user.name,
            USER_STATUS to user.status,
            USER_IMAGE to  user.image
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

    @ExperimentalCoroutinesApi
    suspend fun getUserListFromDb(): Flow<ArrayList<User>?> {
        return callbackFlow {
            val ref = fireStore.collection(USERS).addSnapshotListener { value, error ->
                if(error != null) {
                    this.trySend(null).isFailure
                    error.printStackTrace()
                } else {
                    if(value != null) {
                        val userList = ArrayList<User>()
                        for (item in value.documents) {
                            val userHashMap = item.data as HashMap<*, *>
                            val user = User(
                                name = userHashMap[USER_NAME].toString(),
                                phoneNo = userHashMap[PHONE_NO].toString(),
                                status = userHashMap[USER_STATUS].toString(),
                                fUid = item.id,
                                image = userHashMap[USER_IMAGE].toString(),
                                firebaseToken = userHashMap[FIREBASE_TOKEN].toString()
                            )
                            userList.add(user)
                        }
                        this.trySend(userList).isSuccess
                    }
                }
            }
            awaitClose {
                ref.remove()
            }
        }
    }

    suspend fun updateUserToken(userId: String, map: Map<String,String>): Boolean {
        return suspendCoroutine { callback ->
            fireStore.collection(USERS).document(userId).update(map)
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
}