package com.yml.chatapp.ui.home.chats.message

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R

class ChatMessageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message)
        val messageImage: ImageView = view.findViewById(R.id.image)
}