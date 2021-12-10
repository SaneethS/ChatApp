package com.yml.chatapp.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.firebase.auth.Authentication
import com.yml.chatapp.firebase.firestore.FirebaseGroupDB
import com.yml.chatapp.firebase.firestore.FirebaseUserDB
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private val _getUserDataStatus = MutableLiveData<User>()
    val getUserDataStatus = _getUserDataStatus as LiveData<User>

    private val _getUsersListStatus = MutableLiveData<ArrayList<User>>()
    val getUsersListStatus = _getUsersListStatus as LiveData<ArrayList<User>>

    private val _getGroupListStatus = MutableLiveData<ArrayList<Group>>()
    val getGroupListStatus = _getGroupListStatus as LiveData<ArrayList<Group>>

    fun getUserList() {
        viewModelScope.launch {
            FirebaseUserDB.getInstance().getUserListFromDb().let {
                if(it != null){
                    _getUsersListStatus.value = it
                }
            }
        }
    }

    fun getGroupList(userId: String) {
        viewModelScope.launch {
            FirebaseGroupDB.getInstance().getGroupListFromDb(userId).collect {
                _getGroupListStatus.value = it
            }
        }
    }

    fun getUserData(context: Context) {
        viewModelScope.launch {
            val userId = SharedPref.getInstance(context).getUserId()
            Log.i("HomeData", "user = $userId")
            if (userId != null) {
                FirebaseUserDB.getInstance().getUserFromDb(userId).let {
                    _getUserDataStatus.value = it
                }
            }
        }
    }

    fun logOut(context: Context) {
        Authentication.signOut(context)
    }
}