package com.yml.chatapp.ui.home.groups.participants

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.SharedPref
import com.yml.chatapp.databinding.FragmentParticipantBinding
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.Participants
import com.yml.chatapp.ui.wrapper.User

class ParticipantFragment: Fragment(R.layout.fragment_participant) {
    private lateinit var binding: FragmentParticipantBinding
    private var currentUser: User? = null
    private lateinit var participantsViewModel: ParticipantsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var participantAdapter: ParticipantAdapter
    private var participantList = ArrayList<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentParticipantBinding.bind(view)
        participantsViewModel = ViewModelProvider(requireActivity())[ParticipantsViewModel::class.java]
        currentUser = arguments?.getSerializable("currentUser") as User
        participantsViewModel.getUserList()
        initRecyclerView()
        allListeners()
        allObservers()
    }

    private fun initRecyclerView() {
        participantAdapter = ParticipantAdapter(participantList)
        recyclerView = binding.participantRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = participantAdapter
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            activity?.finish()
        }

        binding.participantsSaveButton.setOnClickListener {
            val checkList = participantAdapter.getSelectedUser()
            val participant = Participants(checkList)
            if(checkList.size != 0) {
                checkList.add(currentUser!!.fUid)
                val groupNameFragment = GroupNameFragment()
                val bundle = Bundle()
                bundle.putSerializable("participant", participant)
                groupNameFragment.arguments = bundle
                activity?.run {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.participants_fragment, groupNameFragment)
                        .addToBackStack(null).commit()
                }
            }else {
                Toast.makeText(context, "Please select the participant", Toast.LENGTH_LONG)
            }
        }
    }

    private fun allObservers() {
        participantsViewModel.getUsersListStatus.observe(viewLifecycleOwner) {  list ->
            participantList.clear()
            participantList.addAll(list)
            list.forEach {
                val userId = SharedPref.getInstance(requireContext()).getUserId()
                if(it.fUid == userId) {
                    participantList.remove(it)
                }
            }
            participantAdapter.notifyDataSetChanged()
        }
    }
}