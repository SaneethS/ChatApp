package com.yml.chatapp.ui.authentication.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
import com.yml.chatapp.firebase.auth.Authentication

class VerifyViewModel:ViewModel() {
    private val _loginstatus = MutableLiveData<Boolean>()
    val loginStatus = _loginstatus as LiveData<Boolean>

    fun loginWithCredentials(credential: PhoneAuthCredential) {
        Authentication.loginWithCredentials(credential) {
            if(it) {
                _loginstatus.value = it
            }
        }
    }
}