package com.yml.chatapp.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.yml.chatapp.R
import com.yml.chatapp.ui.authentication.login.LoginFragment
import com.yml.chatapp.ui.authentication.splashscreen.SplashScreenFragment
import com.yml.chatapp.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]
        val showSplash = intent.getBooleanExtra("showSplash", true)
        if(savedInstanceState == null && showSplash) {
            supportFragmentManager.beginTransaction().
            replace(R.id.fragment_view, SplashScreenFragment()).commit()
        }
        if(savedInstanceState == null && !showSplash) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_view, LoginFragment()).commit()
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