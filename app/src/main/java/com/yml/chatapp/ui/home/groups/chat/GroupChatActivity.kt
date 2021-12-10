package com.yml.chatapp.ui.home.groups.chat

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.GROUP
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.databinding.ActivityGroupChatBinding
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.viewmodelfactory.ViewModelFactory


class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var dialog: Dialog
    private lateinit var groupChatViewModel: GroupChatViewModel
    private lateinit var groupChatAdapter: GroupChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var senderId: String
    private var group: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_screen)
        dialog.show()
        senderId = SharedPref.getInstance(this).getUserId().toString()
        group = intent.getSerializableExtra(GROUP) as Group
        groupChatViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                GroupChatViewModel(group!!)
            )
        )[GroupChatViewModel::class.java]
        binding.groupName.text = group!!.groupName
        allListeners()
        allObservers()
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            sendMessageInGroup()
        }
    }

    private fun allObservers() {
        groupChatViewModel.getGroupChatStatus.observe(this) {
            if(this::groupChatAdapter.isInitialized) {
                groupChatAdapter.notifyDataSetChanged()
            }
        }

        groupChatViewModel.getSenderNameStatus.observe(this) {
            if (it) {
                dialog.dismiss()
                initRecyclerView()
            }
        }
    }

    private fun initRecyclerView() {
        groupChatAdapter = GroupChatAdapter(
            groupChatViewModel.messageList,
            senderId,
            groupChatViewModel.memberList
        )
        recyclerView = binding.groupMessageRecyclerView
        val layoutManager =  LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = groupChatAdapter
    }

    private fun sendMessageInGroup() {
        val message = binding.typeMessageText.text.toString()
        if (message.isNotEmpty()) {
            group?.let { groupChatViewModel.setGroupMessageToDb(senderId, message, it) }
            binding.typeMessageText.setText("")
        } else {
            Toast.makeText(this, "Enter a text", Toast.LENGTH_LONG).show()
        }
    }
}