package com.yml.chatapp.ui.home.groups.chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R

class SendGroupViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sendText: TextView = view.findViewById(R.id.message)
    }