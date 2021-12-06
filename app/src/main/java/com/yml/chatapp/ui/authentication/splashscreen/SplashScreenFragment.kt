package com.yml.chatapp.ui.authentication.splashscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yml.chatapp.R
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.databinding.FragmentSplashScreenBinding
import com.yml.chatapp.ui.authentication.SharedViewModel
import com.yml.chatapp.ui.authentication.login.LoginFragment

class SplashScreenFragment: Fragment(R.layout.fragment_splash_screen) {
    private lateinit var binding: FragmentSplashScreenBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding = FragmentSplashScreenBinding.bind(view)

        binding.splashIcon.alpha = 0f
        binding.splashIcon.animate().setDuration(1500).alpha(1f).withEndAction {
            if(SharedPref.getInstance(requireContext()).getUserId() != null) {
                sharedViewModel.setGoToHomeActivity(true)
            }else{
                activity?.run{
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, LoginFragment()).commit()
                }
            }
        }
    }
}