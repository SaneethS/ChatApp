package com.yml.chatapp.ui.home.groups.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yml.chatapp.common.CONTENT_TEXT
import com.yml.chatapp.data.repository.Repository
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

    private val _sendGroupMessageStatus = MutableLiveData<Message>()
    val sendGroupMessageStatus = _sendGroupMessageStatus as LiveData<Message>

    private val _getGroupPagedStatus = MutableLiveData<ArrayList<Message>>()
    val getGroupPagedStatus = _getGroupPagedStatus as LiveData<ArrayList<Message>>

    init {
        getGroupChatFromDb(group)
        getSenderName(group.participants)
    }

    fun setGroupMessageToDb(senderId:String, message:String, group: Group) {
        viewModelScope.launch {
            Repository.getInstance().sendGroupTextInDb(senderId, message, group).let {
                _sendGroupMessageStatus.postValue(it)
            }
        }
    }

    fun setGroupImageToDb(senderId:String, image: ByteArray, group: Group) {
        viewModelScope.launch {
            Repository.getInstance().sendGroupImageInDb(senderId, image, group).let {
                _sendGroupMessageStatus.postValue(it)
            }
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

    fun getPagedGroupMessage(group: Group, offset: Long) {
        viewModelScope.launch {
            FirebaseGroupDB.getInstance().getPagedGroupMessages(group, offset).let {
                _getGroupPagedStatus.value = it
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

    fun sendGroupNotification(title:String, message: Message) {
        viewModelScope.launch {
            val messageToken = ArrayList<String>()
            memberList.forEach {
                if(it.fUid != message.senderId) {
                    messageToken.add(it.firebaseToken)
                }
            }

            if(message.contentType == CONTENT_TEXT) {
                Repository.getInstance().sendGroupNotification(messageToken, title, message.content, "")
            }else {
                Repository.getInstance().sendGroupNotification(messageToken, title, "", message.content)
            }
        }
    }
}