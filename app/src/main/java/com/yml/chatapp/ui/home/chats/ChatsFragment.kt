package com.yml.chatapp.ui.home.chats

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.OnItemClickListener
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.databinding.FragmentChatBinding
import com.yml.chatapp.ui.home.HomeViewModel
import com.yml.chatapp.ui.home.chats.message.ChatMessageActivity
import com.yml.chatapp.ui.wrapper.User

class ChatsFragment: Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dialog: Dialog
    private lateinit var recyclerView: RecyclerView
    private var currentUser:User = User("","","","",image = "")
    private lateinit var chatListAdapter: ChatListAdapter
    private var userList = ArrayList<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_screen)
        dialog.show()
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        homeViewModel.getUserData(requireContext())
        homeViewModel.getUserList()
        initRecyclerView()
        setItemClickListener()
        allObservers()
    }

    private fun setItemClickListener() {
        chatListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                val sendUser = userList[position]
                val intent = Intent(requireContext(), ChatMessageActivity::class.java)
                intent.putExtra("currentUser",currentUser)
                intent.putExtra("sendUser",sendUser)
                startActivity(intent)
            }
        })
    }

    private fun allObservers() {
        homeViewModel.getUserDataStatus.observe(viewLifecycleOwner) {
            currentUser = it
            dialog.dismiss()
            Log.i("ChatList","$currentUser")
        }

        homeViewModel.getUsersListStatus.observe(viewLifecycleOwner) { list ->
            dialog.dismiss()
            userList.clear()
            userList.addAll(list)
            val currentUserId = context?.let { SharedPref.getInstance(it).getUserId() } ?: ""
            list.forEach {
                if(it.fUid == currentUserId){
                    userList.remove(it)
                }
            }
            chatListAdapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        chatListAdapter = context?.let { ChatListAdapter(it,userList) }!!
        recyclerView = binding.recyclerViewUser
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatListAdapter
    }
}