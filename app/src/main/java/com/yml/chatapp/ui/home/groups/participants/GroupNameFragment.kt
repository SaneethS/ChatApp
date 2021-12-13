package com.yml.chatapp.ui.home.groups.participants

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yml.chatapp.R
import com.yml.chatapp.databinding.FragmentGroupNameBinding
import com.yml.chatapp.ui.home.HomeActivity
import com.yml.chatapp.ui.wrapper.Group
import com.yml.chatapp.ui.wrapper.Participants

class GroupNameFragment : Fragment(R.layout.fragment_group_name) {
    private lateinit var binding: FragmentGroupNameBinding
    private var participants: Participants? = null
    private lateinit var participantsViewModel: ParticipantsViewModel
    private var downloadUri: String = ""
    private var imageUri: Uri? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGroupNameBinding.bind(view)
        participantsViewModel =
            ViewModelProvider(requireActivity())[ParticipantsViewModel::class.java]
        participants = arguments?.getSerializable("participant") as? Participants
        allListeners()
        allObservers()
    }

    private fun allListeners() {
        binding.backButton.setOnClickListener {
            activity?.run {
                supportFragmentManager.popBackStack()
            }
        }

        binding.saveGroupButton.setOnClickListener {
            if(imageUri != null) {
                participantsViewModel.setGroupProfileImage(imageUri!!)
            }else{
                saveGroupDetails()
            }
        }

        binding.groupProfileImage.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data!!
            binding.groupProfileImage.setImageURI(imageUri)
        }
    }

    private fun saveGroupDetails() {
        val groupName = binding.editTextGroupName.text.toString()
        val group =
            participants?.let { participants -> Group(groupName, downloadUri, participants.checkedList) }
        if (groupName.isNotEmpty()) {
            if (group != null) {
                participantsViewModel.setGroupToDb(group)
            }
        } else {
            Toast.makeText(requireContext(), "Please Enter group name", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun allObservers() {
        participantsViewModel.setGroupStatus.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(context, "Failed to save user", Toast.LENGTH_LONG).show()
            }
        }

        participantsViewModel.setGroupProfileStatus.observe(viewLifecycleOwner) {
            downloadUri = it.toString()
            saveGroupDetails()
        }
    }
}