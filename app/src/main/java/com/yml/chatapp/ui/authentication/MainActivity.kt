package com.yml.chatapp.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.yml.chatapp.R
import com.yml.chatapp.ui.authentication.login.LoginFragment
import com.yml.chatapp.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]

        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, LoginFragment()).commit()

//        if(currentUser != null){
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//            finish()
//        }else{
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_view, LoginFragment()).commit()
//        }
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