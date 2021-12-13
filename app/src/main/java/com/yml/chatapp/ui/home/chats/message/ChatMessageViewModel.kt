package com.yml.chatapp.ui.home.chats.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yml.chatapp.common.CONTENT_TEXT
import com.yml.chatapp.data.repository.Repository
import com.yml.chatapp.firebase.firestore.FirebaseChatDB
import com.yml.chatapp.ui.wrapper.Message
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatMessageViewModel(val senderId: String, val receiverId: String): ViewModel() {
    var messageList = ArrayList<Message>()

    private val _getChatsStatus = MutableLiveData<ArrayList<Message>>()
    val getChatStatus = _getChatsStatus as LiveData<ArrayList<Message>>

    private val _sendTextMessageStatus = MutableLiveData<Message>()
    val sendTextMessageStatus = _sendTextMessageStatus as LiveData<Message>

    private val _getPagedMessageStatus = MutableLiveData<ArrayList<Message>>()
    val getPagedMessageStatus = _getPagedMessageStatus as LiveData<ArrayList<Message>>

    init {
        getChatFromDb(senderId, receiverId)
    }

    fun sendTextMessage(senderId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            Repository.getInstance().sendTextToDb(senderId, receiverId, message).let {
                _sendTextMessageStatus.postValue(it)
            }
        }
    }

    fun sendImageMessage(senderId: String, receiverId: String, image: ByteArray) {
        viewModelScope.launch {
            Repository.getInstance().sendImageToDb(senderId, receiverId, image).let {
                _sendTextMessageStatus.postValue(it)
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getChatFromDb(senderId: String, receiverId: String) {
        viewModelScope.launch {
            FirebaseChatDB.getInstance().getChatsFromDb(senderId, receiverId).collect {
                messageList.clear()
                if (it != null) {
                    messageList.addAll(it)
                }
                _getChatsStatus.value = it
            }
        }
    }

    fun sendNotification(token: String, title: String, message: String, imageUrl:String, contentType: String) {
        viewModelScope.launch {
            if(contentType == CONTENT_TEXT) {
                Repository.getInstance().sendPushNotification(token, title, message, "")
            }else {
                Repository.getInstance().sendPushNotification(token, title, "", imageUrl)
            }
        }
    }

    fun getPagedMessages(foreignUser: User?, currentUser: User?, offset: Long) {
        viewModelScope.launch {
            FirebaseChatDB.getInstance().getPagedMessage(foreignUser, currentUser, offset).let {
                _getPagedMessageStatus.value = it
            }
        }
    }
}