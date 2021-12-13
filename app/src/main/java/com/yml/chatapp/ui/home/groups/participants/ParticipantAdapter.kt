package com.yml.chatapp.ui.home.groups.participants

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.ui.wrapper.User

class ParticipantAdapter(private var context: Context, private var participantList: ArrayList<User>):
    RecyclerView.Adapter<ParticipantViewHolder>() {
    private val checkedList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_participants,
            parent, false
        )

        return ParticipantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = participantList[position]
        holder.name.text = participant.name

        if(participant.image.isNotEmpty()) {
            Glide.with(context)
                .load(participant.image)
                .placeholder(R.drawable.whatsapp_user)
                .centerCrop()
                .into(holder.image)
        }

        holder.checkBox.setOnClickListener {
            if(holder.checkBox.isChecked) {
                checkedList.add(participant.fUid)
            }else {
                checkedList.remove(participant.fUid)
            }
        }
    }

    override fun getItemCount(): Int {
        return participantList.size
    }

    fun getSelectedUser():ArrayList<String> {
        return checkedList
    }
}