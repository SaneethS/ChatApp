package com.yml.chatapp.ui.authentication.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yml.chatapp.R
import com.yml.chatapp.databinding.FragmentLoginBinding
import com.yml.chatapp.ui.authentication.SharedViewModel
import com.yml.chatapp.ui.authentication.verify.VerifyFragment
import com.yml.chatapp.ui.wrapper.User

class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        allListeners()
        allObservers()
    }

    private fun allListeners() {
        binding.sendButton.setOnClickListener {
            login()
        }
    }

    private fun allObservers() {
        loginViewModel.verifyUserStatus.observe(viewLifecycleOwner) {
            if(it == true) {
                sharedViewModel.setGoToHomeActivity(true)
            } else {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.sendOtp.observe(viewLifecycleOwner) {
            if(it) {
                val verifyFragment = VerifyFragment()
                val bundle = Bundle()
                bundle.putString("storeVerificationId", loginViewModel.storedVerificationId)
                verifyFragment.arguments = bundle
                activity?.run{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, verifyFragment)
                        .addToBackStack(null).commit()
                }
            }
        }
    }

    private fun login() {
        val number = binding.phoneNumberLogin.text.toString().trim()
        val countryCodePicker = binding.countryCodePicker.selectedCountryCodeWithPlus
        val phoneNumber = countryCodePicker + number
        if(phoneNumber.isNotEmpty()){
            activity?.let { loginViewModel.sendOtp(it, phoneNumber) }
        }else {
            Toast.makeText(requireContext(), "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }
}