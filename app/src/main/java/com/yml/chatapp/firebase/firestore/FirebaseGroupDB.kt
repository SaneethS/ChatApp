package com.yml.chatapp.firebase.firestore

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.CHATS
import com.yml.chatapp.common.GROUPS
import com.yml.chatapp.common.MESSAGES
import com.yml.chatapp.data.model.DbGroup
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.Message
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

class FirebaseGroupDB {
    private var fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseGroupDB? = null
        fun getInstance(): FirebaseGroupDB = instance ?: FirebaseGroupDB()
    }

    suspend fun setGroupToDb(group: Group): Boolean {
        return suspendCoroutine { callback ->
            val dbGroup = DbGroup(group.groupName, group.image, group.participants)
            fireStore.collection(GROUPS).document().set(dbGroup)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        callback.resumeWith(Result.success(true))
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

    suspend fun getGroupListFromDb(userId: String): Flow<ArrayList<Group>?> {
        return callbackFlow {
            val ref = fireStore.collection(GROUPS).whereArrayContains("participants", userId)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        this.trySend(null).isFailure
                        error.printStackTrace()
                    } else {
                        if (value != null) {
                            val groupList = ArrayList<Group>()
                            for (item in value.documents) {
                                val groupHashMap = item.data as HashMap<*, *>
                                val group = Group(
                                    groupName = groupHashMap["groupName"].toString(),
                                    image = groupHashMap["image"].toString(),
                                    participants = groupHashMap["participants"] as ArrayList<String>,
                                    groupId = item.id
                                )
                                groupList.add(group)
                            }
                            this.trySend(groupList).isSuccess
                        }
                    }
                }
            awaitClose {
                ref.remove()
            }
        }

    }

    suspend fun sendGroupMessageInDb(message: Message, group: Group): Message {
        return suspendCoroutine { callback ->
            fireStore.collection(GROUPS).document(group.groupId).collection(MESSAGES)
                .add(message).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback.resumeWith(Result.success(message))
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
    fun getGroupChatsFromDb(group: Group):
            Flow<ArrayList<Message>?> {
        return callbackFlow {

            val ref = fireStore.collection(GROUPS).document(group.groupId)
                .collection(MESSAGES).orderBy(
                    "dateCreated",
                    Query.Direction.DESCENDING
                ).limit(16).addSnapshotListener { value, error ->
                    if (error != null) {
                        this.trySend(null).isFailure
                        error.printStackTrace()
                    } else {
                        if (value != null) {
                            val messageList = arrayListOf<Message>()
                            for (item in value.documents) {
                                val data = item.data as HashMap<*, *>
                                val message = Message(
                                    senderId = data["senderId"].toString(),
                                    dateCreated = data["dateCreated"] as Long,
                                    content = data["content"].toString(),
                                    contentType = data["contentType"].toString()
                                )
                                messageList.add(message)
                            }
                            this.trySend(messageList).isSuccess
                        }
                    }
                }
            awaitClose() {
                ref.remove()
            }
        }
    }

    suspend fun getPagedGroupMessages(group: Group, offset: Long): ArrayList<Message> {
        return suspendCoroutine { callback ->
            Log.i("Group Db", "Control is here")
            fireStore.collection(GROUPS).document(group.groupId)
                .collection(MESSAGES).orderBy(
                    "dateCreated",
                    Query.Direction.DESCENDING
                )
                .startAfter(offset).limit(10).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val messageList = arrayListOf<Message>()
                        for (item in task.result?.documents!!) {
                            val data = item.data as HashMap<*, *>
                            val message = Message(
                                senderId = data["senderId"].toString(),
                                dateCreated = data["dateCreated"] as Long,
                                content = data["content"].toString(),
                                contentType = data["contentType"].toString()
                            )
                            messageList.add(message)
                        }
                        callback.resumeWith(Result.success(messageList))
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

    suspend fun getUsersInfoFromParticipants(userIdList: ArrayList<String>): ArrayList<User> {
        return withContext(Dispatchers.IO) {
            val userList = ArrayList<User>()
            for (id in userIdList) {
                try {
                    val res = FirebaseUserDB.getInstance().getUserFromDb(id)
                    if (res != null) {
                        userList.add(res)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            userList
        }
    }
}
