package com.yml.chatapp.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.yml.chatapp.R
import com.yml.chatapp.databinding.ActivityProfileBinding
import com.yml.chatapp.ui.home.HomeActivity
import com.yml.chatapp.ui.wrapper.User

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var currentUser: User = User("", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileViewModel =
            ViewModelProvider(this@ProfileActivity)[ProfileViewModel::class.java]
        profileViewModel.getUserData(this@ProfileActivity)
        allListeners()
        allObservers()
    }

    private fun allListeners() {
        binding.saveButtonProfile.setOnClickListener {
            editProfile()
        }
    }

    private fun editProfile() {
        val name = binding.nameTextProfile.text.toString().trim()
        val status = binding.statusTextProfile.text.toString().trim()
        val user = User(currentUser.phoneNo, currentUser.fUid, name, status)
        profileViewModel.updateUserData(user)
    }

    private fun allObservers() {
        profileViewModel.getUserDataStatus.observe(this@ProfileActivity) {
            currentUser = it
            setUserDetails()
        }

        profileViewModel.updateUserDataStatus.observe(this@ProfileActivity) {
            if(it) {
                val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setUserDetails() {
        val name:EditText = binding.nameTextProfile
        val status:EditText = binding.statusTextProfile

        name.setText(currentUser.name)
        status.setText(currentUser.status)
    }
}