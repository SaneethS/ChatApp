package com.yml.chatapp.ui.home.groups.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.common.CONTENT_TEXT
import com.yml.chatapp.ui.wrapper.Message
import com.yml.chatapp.ui.wrapper.User

class GroupChatAdapter(
    private val context: Context,
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
                if(message.contentType == CONTENT_TEXT) {
                    holder.sendText.text = message.content
                    holder.sendText.visibility = View.VISIBLE
                    holder.sendImage.visibility = View.GONE
                }else {
                    holder.sendText.visibility = View.GONE
                    holder.sendImage.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(message.content)
                        .placeholder(R.drawable.image_preview)
                        .fitCenter()
                        .into(holder.sendImage)
                }
            }
            else -> {
                holder as ReceiveGroupViewHolder
                memberList.forEach {
                    if (it.fUid == message.senderId) {
                        holder.userName.text = it.name
                    }
                }

                if(message.contentType == CONTENT_TEXT) {
                    holder.receiveText.text = message.content
                    holder.receiveText.visibility = View.VISIBLE
                    holder.receiveImage.visibility = View.GONE
                }else {
                    holder.receiveText.visibility = View.GONE
                    holder.receiveImage.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(message.content)
                        .fitCenter()
                        .into(holder.receiveImage)
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