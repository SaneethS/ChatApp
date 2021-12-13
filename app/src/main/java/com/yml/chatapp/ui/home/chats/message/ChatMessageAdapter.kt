package com.yml.chatapp.ui.home.chats.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yml.chatapp.R
import com.yml.chatapp.common.CONTENT_TEXT
import com.yml.chatapp.ui.wrapper.Message

class ChatMessageAdapter(private val context: Context, private var messageList: ArrayList<Message>,
                            private val senderId: String): RecyclerView.Adapter<ChatMessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = when(viewType) {
            0 -> {
                layoutInflater.inflate(
                    R.layout.sender_chat_layout,
                    parent,
                    false
                )
            }
            else -> {
                layoutInflater.inflate(
                    R.layout.recieve_chat_layout,
                    parent,
                    false
                )
            }
        }

        return ChatMessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = messageList[position]

        if(message.contentType == CONTENT_TEXT) {
            holder.messageText.text = message.content
            holder.messageText.visibility = View.VISIBLE
            holder.messageImage.visibility = View.GONE
        }else {
            holder.messageText.visibility = View.GONE
            holder.messageImage.visibility= View.VISIBLE
            Glide.with(context)
                .load(message.content)
                .placeholder(R.drawable.whatsapp_user)
                .fitCenter()
                .into(holder.messageImage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if(message.senderId == senderId) 0 else 1
    }
    override fun getItemCount(): Int {
        return messageList.size
    }
}