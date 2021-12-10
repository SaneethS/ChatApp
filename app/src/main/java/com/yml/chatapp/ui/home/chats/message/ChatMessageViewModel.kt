package com.yml.chatapp.ui.home.chats.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yml.chatapp.firebase.firestore.FirebaseChatDB
import com.yml.chatapp.ui.wrapper.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatMessageViewModel(val senderId: String, val receiverId: String): ViewModel() {
    var messageList = ArrayList<Message>()

    private val _getChatsStatus = MutableLiveData<Boolean>()
    val getChatStatus = _getChatsStatus as LiveData<Boolean>

    init {
        getChatFromDb(senderId, receiverId)
    }

    fun sendTextMessage(senderId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            FirebaseChatDB.getInstance().sendTextToDb(senderId, receiverId, message)
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
                _getChatsStatus.value = true
            }
        }
    }
}