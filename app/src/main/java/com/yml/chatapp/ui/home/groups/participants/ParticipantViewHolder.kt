package com.yml.chatapp.ui.home.groups.participants

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R

class ParticipantViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val name: TextView = view.findViewById(R.id.participant_user_name)
    val checkBox: CheckBox = view.findViewById(R.id.participants_check)
}