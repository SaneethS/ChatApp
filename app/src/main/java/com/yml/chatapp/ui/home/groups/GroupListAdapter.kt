package com.yml.chatapp.ui.home.groups

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.common.OnItemClickListener
import com.yml.chatapp.ui.wrapper.Group

class GroupListAdapter(private val context: Context, private val groupList: ArrayList<Group>) :
    RecyclerView.Adapter<GroupsViewHolder>() {
    private lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_groups,
            parent,
            false
        )

        return GroupsViewHolder(itemView, clickListener)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val group = groupList[position]

        holder.groupName.text = group.groupName

        if(group.image.isNotEmpty()) {
            Glide.with(context)
                .load(group.image)
                .placeholder(R.drawable.whatsapp_group_user)
                .centerCrop()
                .into(holder.groupImage)
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}