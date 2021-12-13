package com.yml.chatapp.ui.home.groups

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.OnItemClickListener
import de.hdodenhof.circleimageview.CircleImageView

class GroupsViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
    val groupName:TextView = view.findViewById(R.id.list_group_name)
    val groupImage: CircleImageView = view.findViewById(R.id.group_image)

    init {
        itemView.setOnClickListener {
            listener.onItemClick(adapterPosition)
        }
    }
}