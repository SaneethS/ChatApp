package com.yml.chatapp.ui.authentication.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.yml.chatapp.R
import com.yml.chatapp.databinding.FragmentLoginBinding
import com.yml.chatapp.ui.authentication.SharedViewModel
import com.yml.chatapp.ui.home.HomeActivity
import com.yml.chatapp.ui.authentication.verify.VerifyFragment
import java.util.concurrent.TimeUnit

class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var storedVerificationId:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
               sharedViewModel.setGoToHomeActivity(true)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.i("LoginFragment", "exception: $p0")
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId = p0
                resendToken = p1
                val verifyFragment = VerifyFragment()
                val bundle = Bundle()
                bundle.putString("storeVerificationId", storedVerificationId)
                verifyFragment.arguments = bundle
                activity?.run{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, verifyFragment).commit()
                }
            }
        }

        binding.sendButton.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val number = binding.phoneNumberLogin.text.toString().trim()

        if(number.isNotEmpty()){
            sendVerificationCode(number)
        }else {
            Toast.makeText(requireContext(), "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = activity?.let {
            PhoneAuthOptions.newBuilder()
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(it)
                .setCallbacks(callbacks)
                .build()
        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
}