package com.yml.chatapp.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.firebase.firestore.FirebaseUserDB
import com.yml.chatapp.ui.wrapper.User

class ProfileViewModel: ViewModel() {
    private val _updateUserDataStatus = MutableLiveData<Boolean>()
    val updateUserDataStatus = _updateUserDataStatus as LiveData<Boolean>

    private val _getUserDataStatus = MutableLiveData<User>()
    val getUserDataStatus = _getUserDataStatus as LiveData<User>

    fun updateUserData(user: User) {
        FirebaseUserDB.getInstance().updateUserInDb(user) {
            if(it) {
                _updateUserDataStatus.value = it
            }
        }
    }

    fun getUserData(context: Context) {
        val userId = SharedPref.getInstance(context).getUserId()
        Log.i("HomeData", "user = $userId")
        if (userId != null) {
            FirebaseUserDB.getInstance().getUserFromDb(userId) {
                if(it != null) {
                    _getUserDataStatus.value = it
                }
            }
        }
    }
}