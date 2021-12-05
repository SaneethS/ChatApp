package com.yml.chatapp.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    private val _verifyUserStatus = MutableLiveData<Int>()
    val verifyUserStatus = _verifyUserStatus as LiveData<Int>
}