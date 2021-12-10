package com.yml.chatapp.ui.authentication.verify

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.firebase.auth.Authentication
import com.yml.chatapp.firebase.firestore.FirebaseUserDB
import com.yml.chatapp.ui.wrapper.User
import kotlinx.coroutines.launch

class VerifyViewModel:ViewModel() {
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus = _loginStatus as LiveData<Boolean>

    fun loginWithCredentials(context:Context, credential: PhoneAuthCredential) {
        Authentication.loginWithCredentials(credential) { user ->
            viewModelScope.launch {
                if (user != null) {
                    SharedPref.getInstance(context).addUserId(user.fUid)
                    if(user.isNewUser) {
                        FirebaseUserDB.getInstance().setUserToDb(user).let {
                            _loginStatus.value = it
                        }
                    }else{
                        _loginStatus.value = true
                    }
                }
            }
        }
    }
}