package com.yml.chatapp.firebase.firestore

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.CHATS
import com.yml.chatapp.common.MESSAGES
import com.yml.chatapp.ui.wrapper.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.suspendCoroutine

class FirebaseChatDB {
    private var fireStore = Firebase.firestore

    companion object {
        private val instance: FirebaseChatDB? = null
        fun getInstance(): FirebaseChatDB = instance ?: FirebaseChatDB()
    }

    fun chatId(senderId: String, receiverId: String): String {
        if(senderId > receiverId) {
            return senderId+"_"+receiverId
        }else {
            return receiverId+"_"+senderId
        }
    }

    suspend fun sendMessageToDb(chatId: String, senderId: String, receiverId:String, message: Message):Message {
        return suspendCoroutine {   callback ->
            fireStore.collection(CHATS).document(chatId).collection(MESSAGES)
                .add(message).addOnCompleteListener { task->
                    if(task.isSuccessful) {
                        callback.resumeWith(Result.success(message))
                    }else {
                        callback.resumeWith(
                            Result.failure(task.exception ?: Exception("Something went wrong"))
                        )
                    }
                }
        }
    }

    @ExperimentalCoroutinesApi
    fun getChatsFromDb(senderId: String, receiverId: String):
            Flow<ArrayList<Message>?>{
        return callbackFlow {
            val chatId = chatId(senderId, receiverId)

            val ref = fireStore.collection(CHATS).document(chatId)
                .collection(MESSAGES).orderBy("dateCreated",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                    if(error != null) {
                        this.trySend(null).isFailure
                        error.printStackTrace()
                    }else {
                        if(value != null) {
                            val messageList = arrayListOf<Message>()
                            for(item in value.documents) {
                                val data = item.data as HashMap<*,*>
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
}