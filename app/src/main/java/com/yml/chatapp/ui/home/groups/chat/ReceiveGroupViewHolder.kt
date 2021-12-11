package com.yml.chatapp.ui.home.groups.chat

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R

class ReceiveGroupViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val receiveText: TextView = view.findViewById(R.id.receive_message)
        val userName: TextView = view.findViewById(R.id.sender_name)
        val receiveImage: ImageView = view.findViewById(R.id.group_image)
    }