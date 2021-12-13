package com.yml.chatapp.ui.home.chats.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.databinding.ActivityViewProfileBinding
import com.yml.chatapp.ui.wrapper.User

class ViewProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewProfileBinding
    private var foreignUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        foreignUser = intent.getSerializableExtra("foreignUser") as User
        allListeners()
        setUserDetails()
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setUserDetails() {
        val name = binding.userProfileName
        val phone = binding.userPhoneNumber

        name.setText(foreignUser!!.name)
        phone.setText(foreignUser!!.phoneNo)

        if(foreignUser!!.image.isNotEmpty()) {
            Glide.with(this)
                .load(foreignUser!!.image)
                .placeholder(R.drawable.whatsapp_user)
                .centerCrop()
                .into(binding.userImageProfile)
        }
    }
}