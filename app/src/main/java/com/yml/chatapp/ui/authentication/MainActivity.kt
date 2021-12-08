package com.yml.chatapp.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.yml.chatapp.R
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.ui.authentication.login.LoginFragment
import com.yml.chatapp.ui.authentication.splashscreen.SplashScreenFragment
import com.yml.chatapp.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction().
            replace(R.id.fragment_view, SplashScreenFragment()).commit()
        }
        observeNavigation()
    }

    private fun observeNavigation() {
        sharedViewModel.goToHomeActivityStatus.observe(this@MainActivity) {
            if(it) {
                goToHomeActivity()
            }
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}