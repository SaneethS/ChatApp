package com.yml.chatapp.ui.authentication.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthProvider
import com.yml.chatapp.common.CODE_SENT
import com.yml.chatapp.common.VERIFICATION_COMPLETE
import com.yml.chatapp.common.VERIFICATION_FAILED
import com.yml.chatapp.firebase.auth.Authentication

class LoginViewModel: ViewModel() {
    lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val _verifyUserStatus = MutableLiveData<Boolean>()
    val verifyUserStatus = _verifyUserStatus as LiveData<Boolean>

    private val _sendOtp = MutableLiveData<Boolean>()
    val sendOtp = _sendOtp as LiveData<Boolean>

    fun sendOtp(activity: Activity, phoneNo:String) {
        Authentication.sendOtp(activity, phoneNo) { flag, verficationId, token ->
            when(flag) {
                VERIFICATION_COMPLETE -> {
                    _verifyUserStatus.value = true
                }
                VERIFICATION_FAILED -> {
                    _verifyUserStatus.value = false
                }
                CODE_SENT -> {
                    if (verficationId != null) {
                        storedVerificationId = verficationId
                    }
                    if (token != null) {
                        resendToken = token
                    }
                    _sendOtp.value = true
                }
            }
        }
    }
}