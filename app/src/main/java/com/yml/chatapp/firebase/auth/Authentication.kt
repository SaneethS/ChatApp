package com.yml.chatapp.firebase.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yml.chatapp.common.CODE_SENT
import com.yml.chatapp.common.VERIFICATION_COMPLETE
import com.yml.chatapp.common.VERIFICATION_FAILED
import com.yml.chatapp.ui.wrapper.User
import java.util.concurrent.TimeUnit

object Authentication {

    private var auth:FirebaseAuth = FirebaseAuth.getInstance()


    private fun verifyUser(activity:Activity,
                           phoneNo: String,callback: (Int) -> Unit) {

        val callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                callback(VERIFICATION_COMPLETE)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                callback(VERIFICATION_FAILED)
            }

            override fun onCodeSent(verificationId : String, token: PhoneAuthProvider.ForceResendingToken) {
                callback(CODE_SENT)
            }
        }
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNo)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun loginWithCredentials(credential: PhoneAuthCredential, callback: (Boolean) -> Unit) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                callback(true)
            }else {
                callback(false)
            }
        }
    }

}