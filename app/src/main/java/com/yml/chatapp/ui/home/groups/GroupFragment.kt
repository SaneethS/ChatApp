package com.yml.chatapp.ui.home.groups

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.GROUP
import com.yml.chatapp.common.OnItemClickListener
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.databinding.FragmentGroupBinding
import com.yml.chatapp.ui.home.HomeViewModel
import com.yml.chatapp.ui.home.groups.chat.GroupChatActivity
import com.yml.chatapp.ui.home.groups.participants.ParticipantsActivity
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.User

class GroupFragment: Fragment(R.layout.fragment_group) {
    private lateinit var binding: FragmentGroupBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var groupListAdapter: GroupListAdapter
    private lateinit var recyclerView: RecyclerView
    private var currentUser: User = User("","","","","")
    private var groupList: ArrayList<Group> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGroupBinding.bind(view)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        context?.let { homeViewModel.getUserData(it) }
        val userId = context?.let { SharedPref.getInstance(it).getUserId() }
        if (userId != null) {
            homeViewModel.getGroupList(userId)
        }
        initRecyclerView()
        setItemClickListener()
        allListeners()
        allObservers()
    }

    private fun setItemClickListener() {
        groupListAdapter.setOnItemClickListener(object : OnItemClickListener{
            override fun onItemClick(position: Int) {
                val group = groupList[position]
                val intent = Intent(activity, GroupChatActivity::class.java)
                intent.putExtra(GROUP, group)
                startActivity(intent)
            }
        })
    }

    private fun initRecyclerView() {
        groupListAdapter = GroupListAdapter(groupList)
        recyclerView = binding.groupListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = groupListAdapter
    }

    private fun allObservers() {
        homeViewModel.getUserDataStatus.observe(viewLifecycleOwner) {
            currentUser = it
        }

        homeViewModel.getGroupListStatus.observe(viewLifecycleOwner) {
            groupList.clear()
            groupList.addAll(it)
            groupListAdapter.notifyDataSetChanged()
        }
    }

    private fun allListeners() {
        binding.addParticipantsButton.setOnClickListener {
            val intent = Intent(activity, ParticipantsActivity::class.java)
            intent.putExtra("currentUser", currentUser)
            startActivity(intent)
        }
    }
}