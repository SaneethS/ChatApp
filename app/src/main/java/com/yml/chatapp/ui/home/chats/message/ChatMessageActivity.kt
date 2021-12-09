package com.yml.chatapp.ui.home.chats.message

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.databinding.ActivityChatMessageBinding
import com.yml.chatapp.ui.wrapper.User

class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private lateinit var chatMessageViewModel: ChatMessageViewModel
    private var currentUser: User? = null
    private var foreignUser: User? = null
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatMessageViewModel =
            ViewModelProvider(this@ChatMessageActivity)[ChatMessageViewModel::class.java]
        currentUser = intent.getSerializableExtra("currentUser") as User
        foreignUser = intent.getSerializableExtra("sendUser") as User
        binding.sendUserName.text = foreignUser!!.name
        chatMessageViewModel.getChatFromDb(currentUser!!.fUid, foreignUser!!.fUid)
        sendMessage()
        initRecyclerView()
        allListeners()
        allObservers()
    }

    private fun initRecyclerView() {
        chatMessageAdapter = ChatMessageAdapter(chatMessageViewModel.messageList,
            currentUser!!.fUid)
        recyclerView = binding.messageRecyclerView
        val layoutManager =  LinearLayoutManager(this@ChatMessageActivity)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatMessageAdapter
    }

    private fun sendMessage() {
        val message = binding.typeMessageText.text.toString()
        if (message.isNotEmpty()) {
            chatMessageViewModel.sendTextMessage(currentUser!!.fUid, foreignUser!!.fUid, message)
            binding.typeMessageText.setText("")
        }else {
            Toast.makeText(this, "Enter a text", Toast.LENGTH_LONG).show()
        }
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun allObservers() {
        chatMessageViewModel.getChatStatus.observe(this@ChatMessageActivity) {
            chatMessageAdapter.notifyDataSetChanged()
        }
    }
}