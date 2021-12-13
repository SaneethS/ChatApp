package com.yml.chatapp.ui.home.groups.participants

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yml.chatapp.firebase.firestore.FirebaseGroupDB
import com.yml.chatapp.firebase.firestore.FirebaseUserDB
import com.yml.chatapp.firebase.storage.FirebaseStorage
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ParticipantsViewModel: ViewModel() {
    private val _getUsersListStatus = MutableLiveData<ArrayList<User>>()
    val getUsersListStatus = _getUsersListStatus as LiveData<ArrayList<User>>

    private val _setGroupStatus = MutableLiveData<Boolean>()
    val setGroupStatus = _setGroupStatus as LiveData<Boolean>

    private val _setGroupProfileStatus = MutableLiveData<Uri>()
    val setGroupProfileStatus = _setGroupProfileStatus as LiveData<Uri>

    fun getUserList() {
        viewModelScope.launch {
            FirebaseUserDB.getInstance().getUserListFromDb().collect {
                if(it != null){
                    _getUsersListStatus.value = it
                }
            }
        }
    }

    fun setGroupToDb(group: Group) {
        viewModelScope.launch {
            FirebaseGroupDB.getInstance().setGroupToDb(group).let {
                _setGroupStatus.value = it
            }
        }
    }

    fun setGroupProfileImage(profile: Uri) {
        viewModelScope.launch {
            FirebaseStorage.setGroupProfile(profile).let {
                _setGroupProfileStatus.value = it
            }
        }
    }
}