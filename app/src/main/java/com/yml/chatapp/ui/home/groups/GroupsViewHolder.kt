package com.yml.chatapp.ui.home.groups

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.OnItemClickListener

class GroupsViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
    val groupName:TextView = view.findViewById(R.id.list_group_name)

    init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
}