package com.yml.chatapp.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.databinding.ActivityProfileBinding
import com.yml.chatapp.ui.home.HomeActivity
import com.yml.chatapp.ui.wrapper.User

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var currentUser: User = User("", "", "",image = "")
    private var imageUri: Uri? = null
    private var downloadUri: String = ""

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
            if(imageUri != null) {
                profileViewModel.setUserProfile(imageUri!!)
            }else {
                editProfile()
            }
        }
        binding.imageView.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 100)
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            binding.imageView.setImageURI(imageUri)
        }
    }
    private fun editProfile() {
        val name = binding.nameTextProfile.text.toString().trim()
        val status = binding.statusTextProfile.text.toString().trim()
        val user = User(currentUser.phoneNo, currentUser.fUid, name, status, downloadUri)
        if(name.isNotEmpty()) {
            profileViewModel.updateUserData(user)
        }else {
            binding.nameTextProfile.setError("Please Enter the name")
        }
    }

    private fun allObservers() {
        profileViewModel.getUserDataStatus.observe(this@ProfileActivity) {
            currentUser = it
            downloadUri = currentUser.image
            setUserDetails()
        }

        profileViewModel.updateUserDataStatus.observe(this@ProfileActivity) {
            if(it) {
                val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        profileViewModel.setProfileStatus.observe(this@ProfileActivity) {
            downloadUri = it.toString()
            editProfile()
        }
    }

    private fun setUserDetails() {
        val name:EditText = binding.nameTextProfile
        val status:EditText = binding.statusTextProfile

        name.setText(currentUser.name)
        status.setText(currentUser.status)

        if(currentUser.image.isNotEmpty()) {
            Glide.with(this)
                .load(currentUser.image)
                .placeholder(R.drawable.whatsapp_user)
                .centerInside()
                .into(binding.imageView)
        }
    }
}