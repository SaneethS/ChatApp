package com.yml.chatapp.ui.home.chats.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.ui.wrapper.Message

class ChatMessageAdapter(private var messageList: ArrayList<Message>,
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
        holder.messageText.text = message.content
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if(message.senderId == senderId) 0 else 1
    }
    override fun getItemCount(): Int {
        return messageList.size
    }
}