package com.yml.chatapp.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yml.chatapp.ui.home.chats.ChatsFragment
import com.yml.chatapp.ui.home.groups.GroupFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                ChatsFragment()
            }
            1 -> {
                GroupFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}