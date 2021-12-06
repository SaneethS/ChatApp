package com.yml.chatapp.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yml.chatapp.firebase.auth.Authentication

class SharedViewModel: ViewModel() {
    private val _goToHomeActivityStatus = MutableLiveData<Boolean>()
    val goToHomeActivityStatus = _goToHomeActivityStatus as LiveData<Boolean>

    fun setGoToHomeActivity(status: Boolean) {
        _goToHomeActivityStatus.value = status
    }

    fun checkUser():Boolean {
        return Authentication.getCurrentUser() != null
    }
}