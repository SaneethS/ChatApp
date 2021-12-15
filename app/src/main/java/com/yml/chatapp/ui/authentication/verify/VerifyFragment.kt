package com.yml.chatapp.ui.authentication.verify

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.yml.chatapp.R
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.databinding.FragmentVerifyBinding
import com.yml.chatapp.firebase.firestore.FirebaseUserDB
import com.yml.chatapp.ui.authentication.SharedViewModel

class VerifyFragment: Fragment(R.layout.fragment_verify) {
    private lateinit var binding: FragmentVerifyBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var verifyViewModel: VerifyViewModel
    private lateinit var storedVerificationId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentVerifyBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        verifyViewModel = ViewModelProvider(requireActivity())[VerifyViewModel::class.java]
        storedVerificationId = arguments?.getString("storeVerificationId").toString()
        Log.i("VerifyFragment", "$storedVerificationId")

        allListeners()
        allObservers()
    }

    private fun allListeners() {
        binding.button.setOnClickListener {
            val otp = binding.otpText.text.toString().trim()
            if(otp.isNotEmpty()) {
                val credentials:PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId, otp
                )
                context?.let { it1 -> verifyViewModel.loginWithCredentials(it1,credentials) }
            }else{
                Toast.makeText(requireContext(), "Enter the otp", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            activity?.run {
                supportFragmentManager.popBackStack()
            }
        }
    }

    private fun allObservers() {
        verifyViewModel.loginStatus.observe(viewLifecycleOwner) {
            if(it) {
                val uid = context?.let { it1 -> SharedPref.getInstance(it1).getUserId() }
                val token = context?.let { it1 -> SharedPref.getInstance(it1).getFBToken() }
                if (uid != null && token != null) {
                    verifyViewModel.addUserTokenToDb(uid, token)
                    sharedViewModel.setGoToHomeActivity(true)
                }
            }else {
                Toast.makeText(requireContext(),"Invalid OTP",Toast.LENGTH_LONG).show()
            }
        }
    }
}