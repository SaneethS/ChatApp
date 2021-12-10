package com.yml.chatapp.ui.home.groups.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yml.chatapp.firebase.firestore.FirebaseGroupDB
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.Message
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupChatViewModel(val group: Group): ViewModel() {

    var messageList = ArrayList<Message>()
    var memberList = ArrayList<User>()

    private val _getGroupChatStatus = MutableLiveData<Boolean>()
    val getGroupChatStatus = _getGroupChatStatus as LiveData<Boolean>

    private val _getSenderNameStatus = MutableLiveData<Boolean>()
    val getSenderNameStatus = _getSenderNameStatus as LiveData<Boolean>

    init {
        getGroupChatFromDb(group)
        getSenderName(group.participants)
    }

    fun setGroupMessageToDb(senderId:String, message:String, group: Group) {
        viewModelScope.launch {
            FirebaseGroupDB.getInstance().sendGroupTextInDb(senderId, message, group)
        }
    }

    fun getGroupChatFromDb(group: Group) {
        viewModelScope.launch {
            FirebaseGroupDB.getInstance().getGroupChatsFromDb(group).collect {
                messageList.clear()
                if (it != null) {
                    messageList.addAll(it)
                }
                _getGroupChatStatus.value = true
            }
        }
    }

    fun getSenderName(userIdList: ArrayList<String>) {
        viewModelScope.launch {
            val res = FirebaseGroupDB.getInstance().getUsersInfoFromParticipants(userIdList)
            memberList.clear()
            memberList.addAll(res)
            _getSenderNameStatus.value = true
        }
    }
}