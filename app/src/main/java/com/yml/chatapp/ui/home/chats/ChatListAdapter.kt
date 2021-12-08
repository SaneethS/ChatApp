package com.yml.chatapp.ui.home.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yml.chatapp.R
import com.yml.chatapp.common.OnItemClickListener
import com.yml.chatapp.ui.wrapper.User

class ChatListAdapter(private var userList: ArrayList<User>):
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>(){
    private lateinit var clickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_users,
            parent, false
        )

        return ChatListViewHolder(itemView,clickListener)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val item = userList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ChatListViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {
        private val name:TextView = view.findViewById(R.id.list_user_name)
        private val status: TextView = view.findViewById(R.id.list_user_status)
        fun bind(item: User) {
            name.text = item.name
            status.text = item.status
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}