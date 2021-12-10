package com.yml.chatapp.ui.home.groups.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.ui.wrapper.Message
import com.yml.chatapp.ui.wrapper.User

class GroupChatAdapter(
    private var messageList: ArrayList<Message>,
    private var senderId: String,
    private var memberList: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val itemView = layoutInflater.inflate(
                    R.layout.sender_chat_layout,
                    parent, false
                )
                SendGroupViewHolder(itemView)
            }
            else -> {
                val itemView = layoutInflater.inflate(
                    R.layout.receive_group_chat_layout,
                    parent, false
                )

                ReceiveGroupViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (getItemViewType(position)) {
            0 -> {
                holder as SendGroupViewHolder
                holder.sendText.text = message.content
            }
            else -> {
                holder as ReceiveGroupViewHolder
                holder.receiveText.text = message.content
                memberList.forEach {
                    if (it.fUid == message.senderId) {
                        holder.userName.text = it.name
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == senderId) 0 else 1
    }
}