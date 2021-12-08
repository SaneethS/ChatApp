package com.yml.chatapp.ui.home.chats.message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yml.chatapp.R
import com.yml.chatapp.databinding.ActivityChatMessageBinding
import com.yml.chatapp.ui.home.HomeActivity
import com.yml.chatapp.ui.wrapper.User

class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private var sendUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sendUser = intent.getSerializableExtra("sendUser") as User
        binding.sendUserName.text = sendUser!!.name
        allListeners()
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}