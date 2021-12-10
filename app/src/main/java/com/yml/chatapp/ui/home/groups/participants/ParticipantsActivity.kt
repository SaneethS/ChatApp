package com.yml.chatapp.ui.home.groups.participants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yml.chatapp.R
import com.yml.chatapp.databinding.ActivityParticipantsBinding
import com.yml.chatapp.ui.wrapper.User

class ParticipantsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParticipantsBinding
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParticipantsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentUser = intent.getSerializableExtra("currentUser") as User
        goToParticipantFragment()
    }

    private fun goToParticipantFragment() {
        val participantFragment = ParticipantFragment()
        val bundle = Bundle()
        bundle.putSerializable("currentUser", currentUser)
        participantFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.participants_fragment, participantFragment)
            .commit()
    }
}